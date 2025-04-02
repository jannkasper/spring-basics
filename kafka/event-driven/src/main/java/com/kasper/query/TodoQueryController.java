package com.kasper.query;

import com.kasper.model.TodoItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoQueryController {

    private final TodoQueryService queryService;

    @GetMapping
    public ResponseEntity<List<TodoItem>> getAllTodos() {
        return ResponseEntity.ok(queryService.findAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItem> getTodoById(@PathVariable String id) {
        return ResponseEntity.ok(queryService.findTodoById(id));
    }

    @GetMapping("/status/{completed}")
    public ResponseEntity<List<TodoItem>> getTodosByStatus(@PathVariable boolean completed) {
        return ResponseEntity.ok(queryService.findTodosByCompleted(completed));
    }
}