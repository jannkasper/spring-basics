package com.kasper.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/todos/commands")
@RequiredArgsConstructor
public class TodoCommandController {

    private final TodoCommandService commandService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createTodo(@Valid @RequestBody CreateTodoCommand command) {
        String id = commandService.handleCreateTodoCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", id, "message", "Todo item created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateTodo(
            @PathVariable String id,
            @Valid @RequestBody UpdateTodoCommand command) {
        
        command.setId(id);
        commandService.handleUpdateTodoCommand(command);
        
        return ResponseEntity.ok(Map.of("message", "Todo item updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTodo(@PathVariable String id) {
        DeleteTodoCommand command = new DeleteTodoCommand(id);
        commandService.handleDeleteTodoCommand(command);
        
        return ResponseEntity.ok(Map.of("message", "Todo item deleted successfully"));
    }
}