package dev.danilbel.demo.todo.list.api.controller;

import dev.danilbel.demo.todo.list.api.controller.helper.ControllerHelper;
import dev.danilbel.demo.todo.list.api.dto.AskDto;
import dev.danilbel.demo.todo.list.api.dto.ToDoListDto;
import dev.danilbel.demo.todo.list.api.exception.BadRequestException;
import dev.danilbel.demo.todo.list.api.factory.ToDoListFactory;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import dev.danilbel.demo.todo.list.store.repository.ToDoListRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ToDoListController {

    public static final String API_PREFIX = "/api/v1/todo-lists";
    public static final String FETCH_TODO_LIST = API_PREFIX;
    public static final String CREATE_OR_UPDATE_TODO_LIST = API_PREFIX;
    public static final String DELETE_TODO_LIST = API_PREFIX + "/{todo_list_id}";

    ToDoListRepository toDoListRepository;

    ToDoListFactory toDoListFactory;

    ControllerHelper controllerHelper;

    @GetMapping(FETCH_TODO_LIST)
    public List<ToDoListDto> fetchToDoList(
            @RequestParam(value = "optional_prefix_todo_list_name", required = false)
            Optional<String> optionalPrefixToDoListName) {

        optionalPrefixToDoListName = optionalPrefixToDoListName
                .filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ToDoListEntity> toDoListStream = optionalPrefixToDoListName
                .map(toDoListRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(toDoListRepository::streamAllBy);

        return toDoListStream
                .map(toDoListFactory::makeToDoListDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_TODO_LIST)
    public ToDoListDto createOrUpdateToDoList(
            @RequestParam(value = "todo_list_id", required = false) Optional<Long> optionalToDoListId,
            @RequestParam(value = "todo_list_name", required = false) Optional<String> optionalToDoListName) {

        optionalToDoListName = optionalToDoListName
                .filter(toDoListName -> !toDoListName.trim().isEmpty());

        boolean isCreate = optionalToDoListId.isEmpty();

        if (isCreate && optionalToDoListName.isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        final ToDoListEntity toDoList = optionalToDoListId
                .map(controllerHelper::getToDoListOrThrowException)
                .orElseGet(ToDoListEntity::new);

        optionalToDoListName
                .ifPresent(toDoListName -> {
                    toDoListRepository
                            .findByName(toDoListName)
                            .filter(anotherToDoList -> !Objects.equals(anotherToDoList.getId(), toDoList.getId()))
                            .ifPresent(anotherToDoList -> {
                                throw new BadRequestException(
                                        String.format("ToDo list with name \"%s\" already exists.", toDoListName)
                                );
                            });

                    toDoList.setName(toDoListName);

                    if (!isCreate) {
                        toDoList.setUpdatedAt(Instant.now());
                    }
                });

        ToDoListEntity savedToDoList = toDoListRepository.saveAndFlush(toDoList);

        return toDoListFactory.makeToDoListDto(savedToDoList);
    }

    @DeleteMapping(DELETE_TODO_LIST)
    public AskDto deleteToDoList(@PathVariable("todo_list_id") Long toDoListId) {

        ToDoListEntity toDoList = controllerHelper.getToDoListOrThrowException(toDoListId);

        toDoListRepository.delete(toDoList);

        return new AskDto(true);
    }
}
