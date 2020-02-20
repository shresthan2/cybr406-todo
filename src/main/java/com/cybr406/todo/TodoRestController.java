package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class TodoRestController {
    @Autowired
    InMemoryTodoRepository inMemoryTodoRepository;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        if (todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(inMemoryTodoRepository.create(todo), HttpStatus.CREATED);
        }

    }
    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTo(@PathVariable Long id){
        Optional<Todo> list =inMemoryTodoRepository.find(id);
        if (list.isPresent()) {
            Todo test = list.get();
            return new ResponseEntity<>(test, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping ("/todos")
    public List<Todo> findall(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        return inMemoryTodoRepository.findAll(page,size);
    }

    @DeleteMapping ("/todos/{id}")
    public ResponseEntity<Todo> delete(@PathVariable Long id){
        try {
            inMemoryTodoRepository.delete(id);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping ("/todos/{id}/tasks")
    public ResponseEntity<Todo> testall(@PathVariable long id, @RequestBody Task task){
        Todo todo=inMemoryTodoRepository.addTask(id,task);
        return new ResponseEntity<>(todo,HttpStatus.CREATED);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable long id){

        try {
            inMemoryTodoRepository.deleteTask(id);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

