package dev.danilbel.demo.todo.list.api.controller.helper;

import dev.danilbel.demo.todo.list.api.exception.NotFoundException;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
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

    public ToDoListEntity getToDoListOrThrowException(Long toDoListId) {

        return toDoListRepository
                .findById(toDoListId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("ToDo list with id \"%s\" not found.", toDoListId)
                        )
                );
    }
}
