package dev.danilbel.demo.todo.list.api.factory;

import dev.danilbel.demo.todo.list.api.dto.TaskDto;
import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity) {

        return TaskDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isDone(entity.getIsDone())
                .toDoListId(entity.getToDoList().getId())
                .previousTaskId(entity.getPreviousTask().map(TaskEntity::getId).orElse(null))
                .nextTaskId(entity.getNextTask().map(TaskEntity::getId).orElse(null))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
