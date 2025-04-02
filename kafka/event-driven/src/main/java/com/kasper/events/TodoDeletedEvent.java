package com.kasper.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TodoDeletedEvent extends TodoEvent {
    
    public TodoDeletedEvent(String id) {
        super(id, "TODO_DELETED", LocalDateTime.now());
    }
}