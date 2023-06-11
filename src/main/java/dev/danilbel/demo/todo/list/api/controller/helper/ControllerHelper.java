package dev.danilbel.demo.todo.list.api.controller.helper;

import dev.danilbel.demo.todo.list.api.exception.NotFoundException;
import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import dev.danilbel.demo.todo.list.store.repository.TaskRepository;
import dev.danilbel.demo.todo.list.store.repository.ToDoListRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ControllerHelper {

    ToDoListRepository toDoListRepository;
    TaskRepository taskRepository;

    public ToDoListEntity getToDoListOrThrowException(Long toDoListId) {

        return toDoListRepository
                .findById(toDoListId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("ToDo list with id \"%s\" not found.", toDoListId)
                        )
                );
    }

    public TaskEntity getTaskOrThrowException(Long taskId) {

        return taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Task with id \"%s\" not found.", taskId)
                        )
                );
    }
}
