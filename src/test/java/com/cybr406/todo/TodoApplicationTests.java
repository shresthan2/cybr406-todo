package com.cybr406.todo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InMemoryTodoRepository todoRepository;

	@BeforeEach
	public void setup() {
		todoRepository.clear();
	}

	private void generateRandomizedTodos(int total) throws Exception {
		for (int i = 0; i < total; i++) {
			Todo todo = new Todo();
			todo.setAuthor("Test Author " + UUID.randomUUID());
			todo.setDetails("Test details " + UUID.randomUUID());
			mockMvc.perform(post("/todos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(todo)))
					.andDo(print())
					.andExpect(status().isCreated());
		}
	}

	/**
	 * Your first task is to create a new controller class.
	 *
	 * Create a class called TodoRestController in the com.cybr406.todo package.
	 */
	@Test
	public void testTodoRestControllerExists() {
		try {
			Class.forName("com.cybr406.todo.TodoRestController");
		} catch (ClassNotFoundException e) {
			fail("Create a TodoRestController in package 'com.cybr406.todo' for handling requests and responses.");
		}
	}

	/**
	 * Your new controller class needs to have the @RestController annotation at the class level.
	 */
	@Test
	public void testTodoRestControllerAnnotations() {
		try {
			Class clazz = Class.forName("com.cybr406.todo.TodoRestController");
			List<Annotation> annotations = Arrays.asList(clazz.getAnnotations());

			Annotation entityAnnotation = annotations.stream()
					.filter(a -> a.annotationType().getName().equals("org.springframework.web.bind.annotation.RestController"))
					.findFirst()
					.orElse(null);

			assertNotNull(entityAnnotation, "You must add the @RestController annotation to TodoRestController.");
		} catch (ClassNotFoundException e) {
			fail("Create a TodoRestController before annotating it.");
		}
	}

	/**
	 * Create a controller method that accepts a Todo POST'ed as JSON and saves it using an InMemoryTodoRepository.
	 *
	 * Hint: You'll need @PostMapping, @RequestBody, @Autowired and a return type of ResponseEntity<Todo>
	 *     Use @Autowired to grab an InMemoryTodoRepository from the tool bag (ApplicationContext). You'll use this
	 *     class in the controller methods you write.
	 *
	 * Request:
	 *     Path: /todos
	 *     Method: POST
	 *     Content Type: application/json
     * Response:
	 *     Code: 201 Created
	 *     Response Body: A complete Todo object, with newly created value for "id"
	 */
	@Test
	public void testCreateTodoMethod() throws Exception {
		Todo todo = new Todo();
		todo.setAuthor("Mr. Test");
		todo.setDetails("Remember to test.");

		for (long i = 1; i <= 3; i++) {
			String response = mockMvc.perform(post("/todos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(todo)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andReturn()
					.getResponse()
					.getContentAsString();

			Todo result = objectMapper.readValue(response, Todo.class);

			// Verify id increments upwards as new todo's are sent in.
			assertEquals(i, result.getId());
		}
	}

	/**
	 * Add validation to make the "author" and "details" fields required.
	 *
	 * Hint: Try the @NotBlank annotation on the author and details fields. You'll need to add @Valid before @RequestBody.
	 *
	 * Request:
	 *     Path: /todos
	 *     Method: POST
	 *     Content Type: application/json
	 * Response:
	 *     Code: 400 Bad Request (when author or details are null or empty)
	 *     Response Body: Details describing why the Todo was rejected.
	 */
	@Test
	public void testRejectEmptyFields() throws Exception {
		Todo todoNullFields = new Todo();
		// Author remains null
		// Details remains null

		Todo todoBlankFields = new Todo();
		todoBlankFields.setAuthor("");
		todoBlankFields.setDetails("");

		mockMvc.perform(post("/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(todoNullFields)))
				.andDo(print())
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(todoBlankFields)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	/**
	 * Create a method that will return the details of a single todo.
	 *
	 * Hint: You will need @GetMapping and @PathVariable
	 *
	 * Request:
	 *     Path: /todos/{id} where id is the id of a Todo that was previously created.
	 *     Method: GET
	 * Response:
	 *     Code: 200 OK if id exists, 404 Not Found if id does not exist.
	 *     Response Body: The entire todo when the id exists.
	 */
	@Test
	public void testFindTodo() throws Exception {
		Todo todo = new Todo();
		todo.setAuthor("testFindTodo");
		todo.setDetails("testFindTodo");

		mockMvc.perform(post("/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(todo)))
				.andDo(print())
				.andExpect(status().isCreated());

		mockMvc.perform(get("/todos/1"))
				.andDo(print())
				.andExpect(status().isOk());

		mockMvc.perform(get("/todos/999"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	/**
	 * Create a method that will return a paginated subset of results from a large number of todos.
	 *
	 * Hint: You will need @GetMapping @RequestParameter
	 *
	 * Request:
	 *     Path: /todos
	 *     Request parameters: page(default value 0), size(default value of 10)
	 *     Method: GET
	 * Response:
	 *     Code: 200 OK
	 *     Response Body: A big list of todos.
	 */
	@Test
	public void testFindAll() throws Exception {
		generateRandomizedTodos(15);

		// When no params are present, reasonable defaults should be applied.

		String response = mockMvc.perform(get("/todos"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		TypeReference<List<Todo>> typeReference = new TypeReference<List<Todo>>() {};
		List<Todo> todos = objectMapper.readValue(response, typeReference);
		assertEquals(10, todos.size());
		assertEquals(1L, todos.get(0).getId());

		// Size should be adjustable

		response = mockMvc.perform(get("/todos?size=5"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		todos = objectMapper.readValue(response, typeReference);
		assertEquals(5, todos.size());
		assertEquals(1L, todos.get(0).getId());

		// Page should be adjustable

		response = mockMvc.perform(get("/todos?page=1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		todos = objectMapper.readValue(response, typeReference);
		assertEquals(5, todos.size()); // Second page with size 10 should have 5 items.
		assertEquals(11L, todos.get(0).getId());
	}


	/**
	 * After a Todo has been created, individual sub-tasks can be added to it.
	 *
	 * Tasks are steps that need to be completed. They have a boolean component that acts like a "checkbox" and some
	 * details about what needs to be done.
	 *
	 * Request:
	 *     Path: /todos/{id}/tasks where "id" is the id of the parent Todo
	 *     Method: POST
	 * Response:
	 *     Code: 201 Created
	 *     Response Body: The parent Todo the Task was just added to.
	 */
	@Test
	public void testAddTask() throws Exception {
		generateRandomizedTodos(1);

		Map<String, Object> postData = new HashMap<>();
		postData.put("completed", false);
		postData.put("details", "Task 1");

		String response = mockMvc.perform(post("/todos/1/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(postData)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Todo todo = objectMapper.readValue(response, Todo.class);
		assertEquals(1, todo.getTasks().size());

		postData = new HashMap<>();
		postData.put("completed", false);
		postData.put("details", "Task 2");

		response = mockMvc.perform(post("/todos/1/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(postData)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		todo = objectMapper.readValue(response, Todo.class);
		assertEquals(2, todo.getTasks().size());

		Task first = todo.getTasks().get(0);
		Task second = todo.getTasks().get(1);

		assertEquals("Task 1", first.getDetails());
		assertEquals("Task 2", second.getDetails());
	}

	/**
	 * Delete a Todo and ALL of its Tasks with 1 request.
	 *
	 * Hint: Return ResponseEntity without any generic type params instead of void (so you can control response codes).
	 * The TodoRepository's delete method will throw a NoSuchElementFound exception if the Todo does not exist, which
	 * can be handled using a try/catch block in your controller
	 *
	 * Request:
	 *     Path: /todos/{id}
	 *     Method: DELETE
	 * Response:
	 *     Code: 204 No Content on success, 404 Not Found if the todo does not exist.
	 *     Response Body: Nothing...an empty response.
	 */
	@Test
	public void deleteTodo() throws Exception {
		generateRandomizedTodos(1);

		String response = mockMvc.perform(delete("/todos/1"))
				.andDo(print())
				.andExpect(status().isNoContent())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertEquals("", response);

		mockMvc.perform(delete("/todos/1"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	/**
	 * Delete a Todo and ALL of its Tasks with 1 request.
	 *
	 * Hint: Very similar to the last problem, but instead removing a task from an existing todo.
	 *
	 * Request:
	 *     Path: /tasks/{id}
	 *     Method: DELETE
	 * Response:
	 *     Code: 204 No Content on success, 404 Not Found if the task does not exist.
	 *     Response Body: Nothing...an empty response.
	 */
	@Test
	public void deleteTask() throws Exception {
		testAddTask();

		String response = mockMvc.perform(get("/todos/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Todo todo = objectMapper.readValue(response, Todo.class);
		assertEquals(2, todo.getTasks().size());

		mockMvc.perform(delete("/tasks/1"))
				.andDo(print())
				.andExpect(status().isNoContent());

		response = mockMvc.perform(get("/todos/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		todo = objectMapper.readValue(response, Todo.class);
		assertEquals(1, todo.getTasks().size());

		mockMvc.perform(delete("/tasks/999"))
				.andDo(print())
				.andExpect(status().isNotFound());

		mockMvc.perform(delete("/tasks/2"))
				.andDo(print())
				.andExpect(status().isNoContent());

		mockMvc.perform(delete("/tasks/2"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

}
