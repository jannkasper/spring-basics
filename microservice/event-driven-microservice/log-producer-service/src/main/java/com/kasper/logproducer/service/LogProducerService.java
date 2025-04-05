package com.kasper.logproducer.service;

import com.kasper.logproducer.model.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;
    
    @Value("${app.kafka.topic}")
    private String topic;

    public void sendLogEvent(LogEvent logEvent) {
        log.info("Sending log event: {}", logEvent);
        try {
            kafkaTemplate.send(topic, logEvent.getService(), logEvent);
        } catch (Exception e) {
            log.error("Error sending log event to Kafka: {}", e.getMessage(), e);
        }
    }
}