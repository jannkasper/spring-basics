package com.kasper.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoNotificationProcessor {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Create a stream from the todo-events topic
        KStream<String, String> todoEventStream = streamsBuilder.stream(
                KafkaConfig.TODO_EVENTS_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String())
        );

        // Process the events to extract notifications
        todoEventStream.foreach((key, value) -> {
            try {
                processEventForNotification(key, value);
            } catch (Exception e) {
                log.error("Error processing todo event for notification: {}", e.getMessage(), e);
            }
        });
    }

    private void processEventForNotification(String key, String jsonValue) {
        try {
            JsonNode eventNode = objectMapper.readTree(jsonValue);
            String eventType = eventNode.has("type") ? eventNode.get("type").asText() : null;
            String todoId = eventNode.has("id") ? eventNode.get("id").asText() : key;

            if (eventType == null) {
                log.warn("Event type is null, skipping notification");
                return;
            }

            String message = null;
            String notificationType = null;

            switch (eventType) {
                case "TODO_CREATED":
                    String title = eventNode.has("title") ? eventNode.get("title").asText() : "unknown";
                    message = "New todo created: " + title;
                    notificationType = "TODO_CREATED";
                    break;
                case "TODO_UPDATED":
                    title = eventNode.has("title") ? eventNode.get("title").asText() : "unknown";
                    boolean completed = eventNode.has("completed") && eventNode.get("completed").asBoolean();
                    if (completed) {
                        message = "Todo marked as completed: " + title;
                        notificationType = "TODO_COMPLETED";
                    } else {
                        message = "Todo updated: " + title;
                        notificationType = "TODO_UPDATED";
                    }
                    break;
                case "TODO_DELETED":
                    message = "Todo deleted";
                    notificationType = "TODO_DELETED";
                    break;
                default:
                    log.warn("Unknown event type: {}", eventType);
                    return;
            }

            if (message != null && todoId != null) {
                log.info("Creating notification from event: todoId={}, type={}", todoId, notificationType);
                
                // Store in the database
                notificationService.createNotification(todoId, message, notificationType);
                
                // Create notification DTO for Kafka
                NotificationDTO notificationDTO = NotificationDTO.builder()
                        .todoId(todoId)
                        .message(message)
                        .notificationType(notificationType)
                        .timestamp(LocalDateTime.now())
                        .build();
                
                // The notification will be sent to clients via a WebSocket in a real application
                log.info("Notification created: {}", notificationDTO);
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing todo event JSON: {}", e.getMessage(), e);
        }
    }
}