package com.kasper.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TodoCreatedEvent extends TodoEvent {
    private String title;
    private String description;
    
    public TodoCreatedEvent(String id, String title, String description) {
        super(id, "TODO_CREATED", LocalDateTime.now());
        this.title = title;
        this.description = description;
    }
}