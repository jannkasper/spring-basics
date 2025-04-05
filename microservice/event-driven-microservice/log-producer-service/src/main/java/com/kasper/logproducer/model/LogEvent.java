package com.kasper.logproducer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent {
    private Instant timestamp;
    private LogLevel level;
    private String service;
    private String message;
    
    public enum LogLevel {
        INFO, WARN, ERROR
    }
}