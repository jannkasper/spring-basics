package com.kasper.logconsumer.controller;

import com.kasper.logconsumer.model.LogEvent;
import com.kasper.logconsumer.service.LogConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogConsumerService logConsumerService;

    @GetMapping
    public ResponseEntity<List<LogEvent>> getAllLogs() {
        return ResponseEntity.ok(logConsumerService.getRecentLogs());
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<LogEvent>> getLogsByService(@PathVariable String serviceName) {
        return ResponseEntity.ok(logConsumerService.getLogsByService(serviceName));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LogEvent>> getLogsByLevel(@PathVariable LogEvent.LogLevel level) {
        return ResponseEntity.ok(logConsumerService.getLogsByLevel(level));
    }

    @GetMapping("/stats/services")
    public ResponseEntity<Map<String, Integer>> getServiceStats() {
        return ResponseEntity.ok(logConsumerService.getServiceLogCount());
    }

    @GetMapping("/stats/levels")
    public ResponseEntity<Map<LogEvent.LogLevel, Integer>> getLevelStats() {
        return ResponseEntity.ok(logConsumerService.getLogLevelCount());
    }
}