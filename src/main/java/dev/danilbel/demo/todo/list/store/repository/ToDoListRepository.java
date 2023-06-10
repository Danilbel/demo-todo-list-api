package dev.danilbel.demo.todo.list.store.repository;

import dev.danilbel.demo.todo.list.store.entity.ToDoListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface ToDoListRepository extends JpaRepository<ToDoListEntity, Long> {

    Optional<ToDoListEntity> findByName(String name);

    Stream<ToDoListEntity> streamAllBy();

    Stream<ToDoListEntity> streamAllByNameStartsWithIgnoreCase(String prefixName);
}
