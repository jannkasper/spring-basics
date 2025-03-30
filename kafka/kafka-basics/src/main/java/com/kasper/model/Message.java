package com.kasper.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    private String id;
    private String content;
    private LocalDateTime timestamp;
    
    public static Message create(String content) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
}