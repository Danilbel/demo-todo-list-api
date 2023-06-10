package dev.danilbel.demo.todo.list.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TaskDto {

    Long id;

    String title;

    String description;

    @JsonProperty("is_done")
    Boolean isDone;

    @JsonProperty("todo_list_id")
    Long toDoListId;

    @JsonProperty("previous_task_id")
    Long previousTaskId;

    @JsonProperty("next_task_id")
    Long nextTaskId;

    @JsonProperty("created_at")
    Instant createdAt;

    @JsonProperty("updated_at")
    Instant updatedAt;
}
