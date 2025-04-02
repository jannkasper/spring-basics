package com.kasper.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "todo_event_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoEventHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;
    
    private String todoId;
    private String eventType;
    
    @Column(columnDefinition = "TEXT")
    private String eventData; // JSON representation of the event
    
    private LocalDateTime timestamp;
}