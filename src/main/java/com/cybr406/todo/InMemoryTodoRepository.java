package com.cybr406.todo;

import org.springframework.stereotype.Component;

import java.util.*;

import static java.lang.Integer.min;

/**
 * This implementation of TodoRepository uses Lists to save items in memory. It generates unique id's similar to a
 * database using auto-increment.
 *
 * Since this implementation saves everything in memory, all data added or modified while its running will
 * be lost when it is restarted. This can be quite handy for demos and unit tests.
 *
 * You might notice that this class is not threadsafe. What if two requests increment nextTodoId at the same time?
 * Let's not worry about stuff like that for now for the sake of making this code super simple.
 */
@Component
public class InMemoryTodoRepository implements TodoRepository {

    private long nextTodoId = 0;

    private long nextTaskId = 0;

    private List<Todo> todos = new ArrayList<>();

    @Override
    public long count() {
        return todos.size();
    }

    @Override
    public Todo create(Todo todo) {
        todos.add(todo);
        nextTodoId++;
        todo.setId(nextTodoId);
        return todo;
    }

    @Override
    public Optional<Todo> find(Long todoId) {
        return todos.stream()
                .filter(todo -> todo.getId().equals(todoId))
                .findFirst();
    }

    @Override
    public List<Todo> findAll(int page, int size) {
        int offset = page * size;

        if (todos.isEmpty())
            return todos;

        if (offset >= todos.size())
            return Collections.emptyList();

        return todos.subList(offset, min(offset + size, todos.size()));
    }

    @Override
    public Todo addTask(Long todoId, Task task) {
        Todo todo = find(todoId)
                .orElseThrow(NoSuchElementException::new);
        task.setTodo(todo);
        todo.getTasks().add(task);
        nextTaskId++;
        task.setId(nextTaskId);
        return todo;
    }

    @Override
    public void delete(Long id) {
        Todo todo = find(id)
                .orElseThrow(NoSuchElementException::new);
        todos.remove(todo);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = todos.stream()
                .flatMap(t -> t.getTasks().stream())
                .filter(t -> Objects.equals(id, t.getId()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        task.getTodo().getTasks().remove(task);
    }

    public void clear() {
        nextTodoId = 0;
        nextTaskId = 0;
        todos = new ArrayList<>();
    }

}
