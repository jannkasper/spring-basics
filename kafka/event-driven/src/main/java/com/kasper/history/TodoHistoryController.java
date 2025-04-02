package com.kasper.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasper.model.TodoEventHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/todos/history")
@RequiredArgsConstructor
public class TodoHistoryController {
    private final TodoEventHistoryService historyService;
    private final ObjectMapper objectMapper;
    
    @GetMapping("/{todoId}")
    public ResponseEntity<List<HistoryDTO>> getTodoHistory(@PathVariable String todoId) {
        List<TodoEventHistory> historyList = historyService.getHistoryForTodo(todoId);
        
        List<HistoryDTO> historyDTOs = historyList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(historyDTOs);
    }
    
    private HistoryDTO convertToDTO(TodoEventHistory history) {
        JsonNode eventDataJson = null;
        String summary = "";
        
        try {
            eventDataJson = objectMapper.readTree(history.getEventData());
            
            // Create a human-readable summary based on event type
            switch (history.getEventType()) {
                case "TODO_CREATED":
                    String title = eventDataJson.has("title") ? eventDataJson.get("title").asText() : "unknown";
                    summary = "Todo item created with title: " + title;
                    break;
                case "TODO_UPDATED":
                    title = eventDataJson.has("title") ? eventDataJson.get("title").asText() : "unknown";
                    boolean completed = eventDataJson.has("completed") && eventDataJson.get("completed").asBoolean();
                    summary = "Todo item updated to title: " + title + 
                              (completed ? " (marked as completed)" : " (not completed)");
                    break;
                case "TODO_DELETED":
                    summary = "Todo item deleted";
                    break;
                default:
                    summary = "Unknown event: " + history.getEventType();
            }
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing event data: {}", e.getMessage());
            summary = "Error parsing event data";
        }
        
        return HistoryDTO.builder()
                .sequenceId(history.getSequenceId())
                .todoId(history.getTodoId())
                .eventType(history.getEventType())
                .eventData(eventDataJson)
                .timestamp(history.getTimestamp())
                .summary(summary)
                .build();
    }
}