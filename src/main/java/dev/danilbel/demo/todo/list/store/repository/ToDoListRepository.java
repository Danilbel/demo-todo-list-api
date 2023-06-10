package dev.danilbel.demo.todo.list.store.repository;

import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoListRepository extends JpaRepository<ToDoListEntity, Long> {
}
