package com.kasper.query;

import com.kasper.events.TodoCreatedEvent;
import com.kasper.events.TodoDeletedEvent;
import com.kasper.events.TodoUpdatedEvent;
import com.kasper.history.TodoEventHistoryService;
import com.kasper.model.TodoItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoEventHandler {

    private final TodoQueryRepository repository;
    private final TodoEventHistoryService historyService;

    @KafkaListener(topics = "todo-events", groupId = "todo-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleTodoEvent(org.apache.kafka.clients.consumer.ConsumerRecord<String, Object> record, 
                               org.springframework.kafka.support.Acknowledgment ack) {
        try {
            log.info("Received record: topic={}, partition={}, offset={}, key={}", 
                    record.topic(), record.partition(), record.offset(), record.key());
            
            Object value = record.value();
            log.info("Record value class: {}", value != null ? value.getClass().getName() : "null");
            log.info("Record value: {}", value);
            
            // Determine event type based on the type field in the event
            if (value != null) {
                String eventType = null;
                
                // Try to extract type field through reflection
                try {
                    java.lang.reflect.Method getTypeMethod = value.getClass().getMethod("getType");
                    eventType = (String) getTypeMethod.invoke(value);
                    log.info("Event type extracted: {}", eventType);
                } catch (Exception e) {
                    log.warn("Could not extract event type: {}", e.getMessage());
                }
                
                // Store the event in history before processing it
                try {
                    java.lang.reflect.Method getTimestampMethod = value.getClass().getMethod("getTimestamp");
                    java.time.LocalDateTime timestamp = (java.time.LocalDateTime) getTimestampMethod.invoke(value);
                    
                    java.lang.reflect.Method getIdMethod = value.getClass().getMethod("getId");
                    String todoId = (String) getIdMethod.invoke(value);
                    
                    // Save event to history
                    if (eventType != null && todoId != null) {
                        historyService.saveEvent(todoId, eventType, value, timestamp);
                    }
                } catch (Exception e) {
                    log.error("Failed to save event history: {}", e.getMessage(), e);
                }
                
                // Process the event to update the query model
                if ("TODO_CREATED".equals(eventType) && value instanceof TodoCreatedEvent createdEvent) {
                    handleTodoCreatedEvent(createdEvent);
                } else if ("TODO_UPDATED".equals(eventType) && value instanceof TodoUpdatedEvent updatedEvent) {
                    handleTodoUpdatedEvent(updatedEvent);
                } else if ("TODO_DELETED".equals(eventType) && value instanceof TodoDeletedEvent deletedEvent) {
                    handleTodoDeletedEvent(deletedEvent);
                } else {
                    log.warn("Unknown or mismatched event type: {}, class: {}", eventType, value.getClass().getName());
                }
            } else {
                log.warn("Received null record value");
            }
            
            // Acknowledge the message
            if (ack != null) {
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage(), e);
        }
    }

    private void handleTodoCreatedEvent(TodoCreatedEvent event) {
        log.info("Handling TodoCreatedEvent: {}", event);
        
        TodoItem todoItem = TodoItem.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .completed(false)
                .createdAt(event.getTimestamp())
                .updatedAt(event.getTimestamp())
                .build();
        
        repository.save(todoItem);
    }

    private void handleTodoUpdatedEvent(TodoUpdatedEvent event) {
        log.info("Handling TodoUpdatedEvent: {}", event);
        
        repository.findById(event.getId()).ifPresent(todoItem -> {
            todoItem.setTitle(event.getTitle());
            todoItem.setDescription(event.getDescription());
            todoItem.setCompleted(event.isCompleted());
            todoItem.setUpdatedAt(event.getTimestamp());
            
            repository.save(todoItem);
        });
    }

    private void handleTodoDeletedEvent(TodoDeletedEvent event) {
        log.info("Handling TodoDeletedEvent: {}", event);
        repository.deleteById(event.getId());
    }
}