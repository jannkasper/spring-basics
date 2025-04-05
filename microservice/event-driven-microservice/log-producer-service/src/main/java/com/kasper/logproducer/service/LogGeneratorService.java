package com.kasper.logproducer.service;

import com.kasper.logproducer.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogGeneratorService {

    private final LogProducerService logProducerService;
    private final Random random = new Random();
    
    private final List<String> services = List.of(
            "user-service", "product-service", "order-service", "payment-service", "notification-service"
    );
    
    private final List<String> infoMessages = List.of(
            "User logged in successfully",
            "Product details fetched",
            "Order processed successfully",
            "Payment transaction completed",
            "Notification sent to user"
    );
    
    private final List<String> warnMessages = List.of(
            "Slow database query detected",
            "API rate limit approaching threshold",
            "Retry attempt for operation",
            "Cache miss occurred",
            "Service response time degraded"
    );
    
    private final List<String> errorMessages = List.of(
            "Database connection failed",
            "Null pointer exception occurred",
            "API request timeout",
            "Authentication token expired",
            "Invalid input parameter detected"
    );
    
    @Scheduled(fixedRate = 5000) // Generate logs every 5 seconds
    public void generateRandomLogs() {
        // Generate 1-3 random logs
        int logsToGenerate = random.nextInt(3) + 1;
        
        for (int i = 0; i < logsToGenerate; i++) {
            LogEvent logEvent = createRandomLogEvent();
            logProducerService.sendLogEvent(logEvent);
        }
    }
    
    private LogEvent createRandomLogEvent() {
        LogEvent.LogLevel level = getRandomLogLevel();
        String service = services.get(random.nextInt(services.size()));
        String message = getRandomMessage(level);
        
        return LogEvent.builder()
                .timestamp(Instant.now())
                .level(level)
                .service(service)
                .message(message)
                .build();
    }
    
    private LogEvent.LogLevel getRandomLogLevel() {
        // 70% INFO, 20% WARN, 10% ERROR
        int rand = random.nextInt(10);
        if (rand < 7) {
            return LogEvent.LogLevel.INFO;
        } else if (rand < 9) {
            return LogEvent.LogLevel.WARN;
        } else {
            return LogEvent.LogLevel.ERROR;
        }
    }
    
    private String getRandomMessage(LogEvent.LogLevel level) {
        switch (level) {
            case INFO:
                return infoMessages.get(random.nextInt(infoMessages.size()));
            case WARN:
                return warnMessages.get(random.nextInt(warnMessages.size()));
            case ERROR:
                return errorMessages.get(random.nextInt(errorMessages.size()));
            default:
                return "Unknown message";
        }
    }
}