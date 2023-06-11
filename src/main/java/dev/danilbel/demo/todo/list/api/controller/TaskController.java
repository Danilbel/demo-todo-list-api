package dev.danilbel.demo.todo.list.api.controller;

import dev.danilbel.demo.todo.list.api.controller.helper.ControllerHelper;
import dev.danilbel.demo.todo.list.api.dto.AskDto;
import dev.danilbel.demo.todo.list.api.dto.TaskDto;
import dev.danilbel.demo.todo.list.api.exception.BadRequestException;
import dev.danilbel.demo.todo.list.api.exception.NotFoundException;
import dev.danilbel.demo.todo.list.api.factory.TaskDtoFactory;
import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import dev.danilbel.demo.todo.list.store.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    public static final String API_PREFIX = "/api/v1/tasks";
    public static final String API_FETCH_AND_CREATE_PREFIX = ToDoListController.API_PREFIX + "/{todo_list_id}/tasks";
    public static final String FETCH_TASKS = API_FETCH_AND_CREATE_PREFIX;
    public static final String CREATE_TASK = API_FETCH_AND_CREATE_PREFIX;
    public static final String UPDATE_TASK = API_PREFIX + "/{task_id}";
    public static final String CHANGE_TASK_POSITION = API_PREFIX + "/{task_id}/position/change";
    public static final String DELETE_TASK = API_PREFIX + "/{task_id}";

    TaskRepository taskRepository;

    TaskDtoFactory taskDtoFactory;

    ControllerHelper controllerHelper;

    @GetMapping(FETCH_TASKS)
    public List<TaskDto> fetchTasks(
            @PathVariable("todo_list_id") Long toDoListId,
            @RequestParam(value = "optional_prefix_task_title", required = false)
            Optional<String> optionalPrefixTaskTitle) {

        final Optional<String> finalOptionalPrefixTaskTitle = optionalPrefixTaskTitle
                .filter(prefixName -> !prefixName.trim().isEmpty());

        ToDoListEntity toDoList = controllerHelper.getToDoListOrThrowException(toDoListId);

        return toDoList
                .getTasks()
                .stream()
                .filter(task -> finalOptionalPrefixTaskTitle
                        .map(prefixTaskTitle -> task.getTitle().toLowerCase().startsWith(prefixTaskTitle.toLowerCase()))
                        .orElse(true))
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_TASK)
    public TaskDto createTask(
            @PathVariable("todo_list_id") Long toDoListId,
            @RequestParam("task_title") String taskTitle,
            @RequestParam(value = "task_description", required = false) Optional<String> optionalTaskDescription) {

        optionalTaskDescription = optionalTaskDescription
                .filter(description -> !description.trim().isEmpty());

        if (taskTitle.trim().isEmpty()) {
            throw new BadRequestException("Task title can't be empty.");
        }

        ToDoListEntity toDoList = controllerHelper.getToDoListOrThrowException(toDoListId);

        Optional<TaskEntity> optionalAnotherTask = Optional.empty();
        for (TaskEntity task : toDoList.getTasks()) {
            if (task.getTitle().equalsIgnoreCase(taskTitle)) {
                throw new BadRequestException(
                        String.format("Task with title \"%s\" already exists.", taskTitle)
                );
            }

            if (task.getNextTask().isEmpty()) {
                optionalAnotherTask = Optional.of(task);
                break;
            }
        }

        TaskEntity task = taskRepository
                .saveAndFlush(
                        TaskEntity.builder()
                                .title(taskTitle)
                                .description(optionalTaskDescription.orElse(null))
                                .toDoList(toDoList)
                                .build()
                );

        optionalAnotherTask
                .ifPresent(anotherTask -> {
                            task.setPreviousTask(anotherTask);
                            anotherTask.setNextTask(task);

                            taskRepository.saveAndFlush(anotherTask);
                        }
                );

        TaskEntity savedTask = taskRepository.saveAndFlush(task);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    @PatchMapping(UPDATE_TASK)
    public TaskDto updateTask(
            @PathVariable("task_id") Long taskId,
            @RequestParam(value = "task_title", required = false) Optional<String> optionalTaskTitle,
            @RequestParam(value = "task_description", required = false) Optional<String> optionalTaskDescription,
            @RequestParam(value = "task_is_done", required = false) Optional<Boolean> optionalTaskIsDone) {

        if (optionalTaskTitle.isEmpty() && optionalTaskDescription.isEmpty() && optionalTaskIsDone.isEmpty()) {
            throw new BadRequestException("Task title, description and is done can't be empty at the same time.");
        }

        TaskEntity task = controllerHelper.getTaskOrThrowException(taskId);

        optionalTaskTitle
                .ifPresent(
                        taskTitle -> {

                            if (taskTitle.trim().isEmpty()) {
                                throw new BadRequestException("Task title can't be empty.");
                            }

                            taskRepository
                                    .findTaskEntityByToDoListIdAndTitleContainsIgnoreCase(
                                            task.getToDoList().getId(),
                                            taskTitle
                                    )
                                    .ifPresent(anotherTask -> {
                                        if (!anotherTask.getId().equals(task.getId())) {
                                            throw new BadRequestException(
                                                    String.format("Task with title \"%s\" already exists.", taskTitle)
                                            );
                                        }
                                    });

                            task.setTitle(taskTitle);
                        }
                );

        optionalTaskDescription
                .ifPresent(taskDescription -> task.setDescription(
                        taskDescription.trim().isBlank() ? null : taskDescription.trim())
                );

        optionalTaskIsDone
                .ifPresent(task::setIsDone);

        task.setUpdatedAt(Instant.now());

        TaskEntity savedTask = taskRepository.saveAndFlush(task);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    @PatchMapping(CHANGE_TASK_POSITION)
    public TaskDto changeTaskPosition(
            @PathVariable("task_id") Long taskId,
            @RequestParam(value = "new_previous_task_id", required = false) Optional<Long> optionalNewPreviousTaskId) {

        final TaskEntity task = controllerHelper.getTaskOrThrowException(taskId);

        Optional<Long> optionalOldPreviousTaskId = task.getPreviousTask()
                .map(TaskEntity::getId);

        if (optionalNewPreviousTaskId.equals(optionalOldPreviousTaskId)) {
            return taskDtoFactory.makeTaskDto(task);
        }

        Optional<TaskEntity> optionalNewPreviousTask =
                getOptionalNewPreviousTaskOrThrowException(task, optionalNewPreviousTaskId);

        Optional<TaskEntity> optionalNewNextTask = optionalNewPreviousTask
                .map(TaskEntity::getNextTask)
                .orElseGet(() -> task
                        .getToDoList()
                        .getTasks()
                        .stream()
                        .filter(anotherTask -> anotherTask.getPreviousTask().isEmpty())
                        .findFirst()
                );

        replaceOldTaskPosition(task);

        TaskEntity savedTask = replaceNewTaskPosition(task, optionalNewPreviousTask, optionalNewNextTask);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    @DeleteMapping(DELETE_TASK)
    public AskDto deleteTask(@PathVariable("task_id") Long taskId) {

        TaskEntity task = controllerHelper.getTaskOrThrowException(taskId);

        replaceOldTaskPosition(task);

        taskRepository.delete(task);

        return new AskDto(true);
    }

    private Optional<TaskEntity> getOptionalNewPreviousTaskOrThrowException(
            TaskEntity task, Optional<Long> optionalNewPreviousTaskId) {

        ToDoListEntity toDoList = task.getToDoList();

        return optionalNewPreviousTaskId
                .map(newPreviousTaskId -> {

                    if (newPreviousTaskId.equals(task.getId())) {
                        throw new BadRequestException(
                                String.format("Task with id \"%s\" can't be previous task for itself.", task.getId())
                        );
                    }

                    TaskEntity newPreviousTask = controllerHelper.getTaskOrThrowException(newPreviousTaskId);
                    if (!Objects.equals(newPreviousTask.getToDoList().getId(), toDoList.getId())) {
                        throw new BadRequestException(
                                String.format(
                                        "Task with id \"%s\" and task with id \"%s\" are not in the same to do list.",
                                        task.getId(),
                                        newPreviousTaskId
                                )
                        );
                    }

                    return newPreviousTask;
                });
    }

    private void replaceOldTaskPosition(TaskEntity task) {
        Optional<TaskEntity> optionalOldPreviousTask = task.getPreviousTask();
        Optional<TaskEntity> optionalOldNextTask = task.getNextTask();

        optionalOldPreviousTask
                .ifPresent(oldPreviousTask -> {
                    oldPreviousTask.setNextTask(optionalOldNextTask.orElse(null));

                    taskRepository.saveAndFlush(oldPreviousTask);
                });

        optionalOldNextTask
                .ifPresent(oldNextTask -> {
                    oldNextTask.setPreviousTask(optionalOldPreviousTask.orElse(null));

                    taskRepository.saveAndFlush(oldNextTask);
                });
    }

    private TaskEntity replaceNewTaskPosition(
            TaskEntity task,
            Optional<TaskEntity> optionalNewPreviousTask,
            Optional<TaskEntity> optionalNewNextTask) {

        if (optionalNewPreviousTask.isPresent()) {
            TaskEntity newPreviousTask = optionalNewPreviousTask.get();
            newPreviousTask.setNextTask(task);
            task.setPreviousTask(newPreviousTask);
        } else {
            task.setPreviousTask(null);
        }

        if (optionalNewNextTask.isPresent()) {
            TaskEntity newNextTask = optionalNewNextTask.get();
            newNextTask.setPreviousTask(task);
            task.setNextTask(newNextTask);
        } else {
            task.setNextTask(null);
        }

        optionalNewPreviousTask.ifPresent(taskRepository::saveAndFlush);
        optionalNewNextTask.ifPresent(taskRepository::saveAndFlush);

        return taskRepository.saveAndFlush(task);
    }
}
