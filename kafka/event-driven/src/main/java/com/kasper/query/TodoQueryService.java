package com.kasper.query;

import com.kasper.model.TodoItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoQueryRepository repository;

    public List<TodoItem> findAllTodos() {
        return repository.findAll();
    }

    public TodoItem findTodoById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo item not found with id: " + id));
    }

    public List<TodoItem> findTodosByCompleted(boolean completed) {
        return repository.findByCompleted(completed);
    }
}