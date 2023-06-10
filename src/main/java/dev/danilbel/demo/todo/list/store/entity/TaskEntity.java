package dev.danilbel.demo.todo.list.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Optional;

@Entity
@Table(name = "task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String title;

    String description;

    @Builder.Default
    Boolean isDone = false;

    @ManyToOne
    ToDoListEntity toDoList;

    @OneToOne
    TaskEntity previousTask;

    @OneToOne
    TaskEntity nextTask;

    @Builder.Default
    Instant createdAt = Instant.now();

    @Builder.Default
    Instant updatedAt = Instant.now();

    public Optional<TaskEntity> getPreviousTask() {
        return Optional.ofNullable(previousTask);
    }

    public Optional<TaskEntity> getNextTask() {
        return Optional.ofNullable(nextTask);
    }
}
