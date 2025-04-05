package com.kasper.logproducer.controller;

import com.kasper.logproducer.model.LogEvent;
import com.kasper.logproducer.service.LogProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {

    private final LogProducerService logProducerService;

    @PostMapping
    public ResponseEntity<String> publishLog(@RequestBody LogEvent logEvent) {
        try {
            // Set timestamp if not provided
            if (logEvent.getTimestamp() == null) {
                logEvent.setTimestamp(Instant.now());
            }
            
            logProducerService.sendLogEvent(logEvent);
            return ResponseEntity.ok("Log event published successfully");
        } catch (Exception e) {
            log.error("Error publishing log event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to publish log event: " + e.getMessage());
        }
    }
}