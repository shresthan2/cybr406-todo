package com.cybr406.todo.ungraded;

import com.cybr406.todo.InMemoryTodoRepository;
import com.cybr406.todo.Task;
import com.cybr406.todo.Todo;
import com.cybr406.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTodoRepositoryTests {

    TodoRepository todoRepository;

    @BeforeEach
    public void setup() {
        todoRepository = new InMemoryTodoRepository();
    }

    private void generateRandomizedTodos(int total) {
        for (int i = 0; i < total; i++) {
            Todo todo = new Todo();
            todo.setAuthor("Test Author " + UUID.randomUUID());
            todo.setDetails("Test details " + UUID.randomUUID());
            todoRepository.create(todo);
        }
    }

    private void generateRandomizedTasks(Todo todo, int total) {
        for (int i = 0; i < total; i++) {
            Task task = new Task(todo);
            task.setCompleted(false);
            task.setDetails("Details " + UUID.randomUUID());
            todoRepository.addTask(todo.getId(), task);
        }
    }

    @Test
    public void testCreate() {
        Todo todo = new Todo();
        todo.setAuthor("Test Author");
        todo.setDetails("Test details.");

        Todo result = todoRepository.create(todo);
        assertEquals(1, result.getId());
        assertEquals(todo.getAuthor(), result.getAuthor());
        assertEquals(todo.getDetails(), result.getDetails());
        assertEquals(0, result.getTasks().size());
    }

    @Test
    public void testFind() {
        testCreate();
        assertFalse(todoRepository.find(0L).isPresent());
        assertTrue(todoRepository.find(1L).isPresent());
    }

    private void assertPageElement(long expectedId, int pagePosition, List<Todo> page) {
        Todo expectedTodo = todoRepository.find(expectedId)
                .orElseThrow(NoSuchElementException::new);
        assertEquals(expectedTodo, page.get(pagePosition));
    }

    @Test
    public void testFindAll() {
        generateRandomizedTodos(15);

        List<Todo> page = todoRepository.findAll(0, 10);
        assertEquals(10, page.size());
        assertPageElement(1, 0, page);
        assertPageElement(10, 9, page);

        page = todoRepository.findAll(1, 10);
        assertEquals(5, page.size());
        assertPageElement(11, 0, page);
        assertPageElement(15, 4, page);

        page = todoRepository.findAll(0, 5);
        assertEquals(5, page.size());
        assertPageElement(1, 0, page);
        assertPageElement(5, 4, page);

        page = todoRepository.findAll(1, 5);
        assertEquals(5, page.size());
        assertPageElement(6, 0, page);
        assertPageElement(10, 4, page);

        page = todoRepository.findAll(2, 5);
        assertEquals(5, page.size());
        assertPageElement(11, 0, page);
        assertPageElement(15, 4, page);

        page = todoRepository.findAll(3, 5);
        assertEquals(0, page.size());
    }

    @Test
    public void testCount() {
        generateRandomizedTodos(15);
        assertEquals(15, todoRepository.count());
    }

    @Test
    public void testAddTask() {
        Todo todo = new Todo();
        todo.setAuthor("Test Author");
        todo.setDetails("Test details.");
        Todo result = todoRepository.create(todo);

        Task task = new Task(result);
        task.setCompleted(false);
        task.setDetails("Do some stuff.");

        result = todoRepository.addTask(result.getId(), task);
        assertEquals(1, result.getTasks().size());
        assertEquals(result.getTasks().get(0), task);

        Task task2 = new Task(result);
        task.setCompleted(false);
        task.setDetails("Do more stuff.");

        result = todoRepository.addTask(result.getId(), task2);
        assertEquals(2, result.getTasks().size());
        assertEquals(result.getTasks().get(1), task2);
    }

    @Test
    public void testDelete() {
        generateRandomizedTodos(15);
        todoRepository.delete(1L);
        assertEquals(14, todoRepository.count());
    }

    @Test
    public void testDeleteTask() {
        generateRandomizedTodos(1);
        Todo todo = todoRepository.find(1L)
                .orElseThrow(NoSuchElementException::new);
        generateRandomizedTasks(todo, 5);

        assertEquals(5, todo.getTasks().size());

        Task first = todo.getTasks().get(0);

        todoRepository.deleteTask(first.getId());
        assertEquals(4, todo.getTasks().size());
        assertFalse(todo.getTasks().stream()
                .anyMatch(t -> Objects.equals(1L, t.getId())));
    }

}
