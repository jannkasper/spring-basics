package com.kasper.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.model.TodoEventHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoEventHistoryService {
    private final TodoEventHistoryRepository repository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public void saveEvent(String todoId, String eventType, Object eventData, LocalDateTime timestamp) {
        log.info("Saving event history: todoId={}, eventType={}", todoId, eventType);
        
        TodoEventHistory history = TodoEventHistory.builder()
                .todoId(todoId)
                .eventType(eventType)
                .timestamp(timestamp)
                .build();
        
        try {
            history.setEventData(objectMapper.writeValueAsString(eventData));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize event data", e);
        }
        
        repository.save(history);
    }
    
    public List<TodoEventHistory> getHistoryForTodo(String todoId) {
        return repository.findByTodoIdOrderByTimestampAsc(todoId);
    }
}