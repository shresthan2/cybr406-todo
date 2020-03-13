package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class TodoRestController {
    @Autowired
    TodoJpaRepository todoJpaRepository;

    @Autowired
    TaskJpaRepository taskJpaRepository;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        if (todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(todoJpaRepository.save(todo), HttpStatus.CREATED);
        }

    }
    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTo(@PathVariable Long id){
        Optional<Todo> list =todoJpaRepository.findById(id);
        if (list.isPresent()) {
            Todo test = list.get();
            return new ResponseEntity<>(test, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping ("/todos")
    public Page<Todo> findAll(Pageable page){
        return (Page<Todo>) todoJpaRepository.findAll( page);
    }

    @DeleteMapping ("/todos/{id}")
    public ResponseEntity delete(@PathVariable long id){
        if (todoJpaRepository.existsById(id)){
            todoJpaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else
        return new ResponseEntity(HttpStatus.NOT_FOUND) ;
    }

    @PostMapping ("/todos/{id}/tasks")
    public ResponseEntity<Todo> testall(@PathVariable long id, @Valid@RequestBody Task task) {
        Optional<Todo> optionalTodo = todoJpaRepository.findById(id);
        if (optionalTodo.isPresent()) {
            Todo todo= optionalTodo.get();
            todo.getTasks().add(task);
            task.setTodo(todo);
            taskJpaRepository.save(task);
            return  new ResponseEntity<>(todo,HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable long id) {
        if (taskJpaRepository.existsById(id)) {
            taskJpaRepository.deleteById(id);
            todoJpaRepository.existsById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

}

