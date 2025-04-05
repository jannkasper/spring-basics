package com.kasper.logconsumer.service;

import com.kasper.logconsumer.model.LogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class LogConsumerService {

    // In-memory storage for logs (could be replaced with a database in production)
    private final List<LogEvent> recentLogs = new ArrayList<>();
    private final int MAX_LOGS = 1000; // Maximum number of logs to keep in memory
    
    // Count logs by service and level
    private final ConcurrentMap<String, Integer> serviceLogCount = new ConcurrentHashMap<>();
    private final ConcurrentMap<LogEvent.LogLevel, Integer> logLevelCount = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(LogEvent logEvent) {
        try {
            // Log the event
            String logColor = getColorByLogLevel(logEvent.getLevel());
            log.info("{}Received log event: {} | {} | {} | {}{}",
                    logColor,
                    logEvent.getTimestamp(),
                    logEvent.getLevel(),
                    logEvent.getService(),
                    logEvent.getMessage(),
                    "\u001B[0m"); // Reset color

            // Store the log in memory
            synchronized (recentLogs) {
                recentLogs.add(logEvent);
                // Remove oldest log if we exceed the maximum
                if (recentLogs.size() > MAX_LOGS) {
                    recentLogs.remove(0);
                }
            }
            
            // Update statistics
            updateStatistics(logEvent);
            
            // Special handling for ERROR logs
            if (logEvent.getLevel() == LogEvent.LogLevel.ERROR) {
                handleErrorLog(logEvent);
            }
        } catch (Exception e) {
            log.error("Error processing log event: {}", e.getMessage(), e);
        }
    }
    
    private String getColorByLogLevel(LogEvent.LogLevel level) {
        switch (level) {
            case INFO:
                return "\u001B[32m"; // Green
            case WARN:
                return "\u001B[33m"; // Yellow
            case ERROR:
                return "\u001B[31m"; // Red
            default:
                return "";
        }
    }
    
    private void updateStatistics(LogEvent logEvent) {
        // Update service count
        serviceLogCount.compute(logEvent.getService(), (k, v) -> (v == null) ? 1 : v + 1);
        
        // Update log level count
        logLevelCount.compute(logEvent.getLevel(), (k, v) -> (v == null) ? 1 : v + 1);
    }
    
    private void handleErrorLog(LogEvent logEvent) {
        // This is where you could implement additional logic for error logs
        // such as sending notifications, triggering alerts, etc.
        log.warn("ERROR log detected from service [{}]: {}", 
                logEvent.getService(), logEvent.getMessage());
    }
    
    // Methods to access log data
    
    public List<LogEvent> getRecentLogs() {
        synchronized (recentLogs) {
            return new ArrayList<>(recentLogs);
        }
    }
    
    public List<LogEvent> getLogsByService(String service) {
        synchronized (recentLogs) {
            return recentLogs.stream()
                    .filter(log -> log.getService().equals(service))
                    .toList();
        }
    }
    
    public List<LogEvent> getLogsByLevel(LogEvent.LogLevel level) {
        synchronized (recentLogs) {
            return recentLogs.stream()
                    .filter(log -> log.getLevel() == level)
                    .toList();
        }
    }
    
    public ConcurrentMap<String, Integer> getServiceLogCount() {
        return serviceLogCount;
    }
    
    public ConcurrentMap<LogEvent.LogLevel, Integer> getLogLevelCount() {
        return logLevelCount;
    }
}