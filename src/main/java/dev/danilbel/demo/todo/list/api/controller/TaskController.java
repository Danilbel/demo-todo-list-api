package dev.danilbel.demo.todo.list.api.controller;

import dev.danilbel.demo.todo.list.api.controller.helper.ControllerHelper;
import dev.danilbel.demo.todo.list.api.dto.TaskDto;
import dev.danilbel.demo.todo.list.api.exception.BadRequestException;
import dev.danilbel.demo.todo.list.api.exception.NotFoundException;
import dev.danilbel.demo.todo.list.api.factory.TaskDtoFactory;
import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import dev.danilbel.demo.todo.list.store.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    public static final String API_FETCH_AND_CREATE_PREFIX = ToDoListController.API_PREFIX + "/{todo_list_id}/tasks";
    public static final String FETCH_TASKS = API_FETCH_AND_CREATE_PREFIX;
    public static final String CREATE_TASK = API_FETCH_AND_CREATE_PREFIX;

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
            throw new NotFoundException("Task title can't be empty.");
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
}
