package com.cybr406.todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    long count();

    Todo create(Todo todo);

    Optional<Todo> find(Long todoId);

    List<Todo> findAll(int page, int size);

    Todo addTask(Long todoId, Task task);

    void delete(Long id);

    void deleteTask(Long id);

}
