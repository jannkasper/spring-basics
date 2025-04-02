package com.kasper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todo_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItem {
    
    @Id
    private String id;
    
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TodoItem create(String title, String description) {
        return TodoItem.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .description(description)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public void update(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }
}