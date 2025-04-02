package com.kasper.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TodoUpdatedEvent extends TodoEvent {
    private String title;
    private String description;
    private boolean completed;
    
    public TodoUpdatedEvent(String id, String title, String description, boolean completed) {
        super(id, "TODO_UPDATED", LocalDateTime.now());
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}