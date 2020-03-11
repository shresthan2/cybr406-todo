package com.cybr406.todo;

import com.cybr406.todo.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoJpaRepository extends JpaRepository<Task,Long> {
}
