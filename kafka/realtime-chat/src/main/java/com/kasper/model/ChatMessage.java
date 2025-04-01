package com.kasper.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;
    private String senderId;
    private String content;
    private String roomId;
    
    @Field(targetType = FieldType.INT64)
    private LocalDateTime timestamp;
    
    private MessageType type;
    private boolean persisted;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}