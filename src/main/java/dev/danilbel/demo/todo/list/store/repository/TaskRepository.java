package dev.danilbel.demo.todo.list.store.repository;

import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findTaskEntityByToDoListIdAndTitleContainsIgnoreCase(Long toDoListId, String taskTitle);
}
