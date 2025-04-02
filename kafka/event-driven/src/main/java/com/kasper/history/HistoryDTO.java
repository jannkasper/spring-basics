package com.kasper.history;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistoryDTO {
    private Long sequenceId;
    private String todoId;
    private String eventType;
    private JsonNode eventData;
    private LocalDateTime timestamp;
    private String summary;
}