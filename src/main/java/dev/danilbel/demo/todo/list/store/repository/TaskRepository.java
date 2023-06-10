package dev.danilbel.demo.todo.list.store.repository;

import dev.danilbel.demo.todo.list.store.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
