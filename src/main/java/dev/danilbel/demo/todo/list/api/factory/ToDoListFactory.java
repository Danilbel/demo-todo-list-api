package dev.danilbel.demo.todo.list.api.factory;

import dev.danilbel.demo.todo.list.api.dto.ToDoListDto;
import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ToDoListFactory {

    TaskDtoFactory taskDtoFactory;

    public ToDoListDto makeToDoListDto(ToDoListEntity entity) {

        return ToDoListDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tasks(
                        entity
                                .getTasks()
                                .stream()
                                .map(taskDtoFactory::makeTaskDto)
                                .collect(Collectors.toList())
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
