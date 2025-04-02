package com.kasper.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TodoEvent {
    private String id;
    private String type;
    private LocalDateTime timestamp;
}