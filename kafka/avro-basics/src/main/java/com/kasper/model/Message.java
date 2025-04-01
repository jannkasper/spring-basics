package com.kasper.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    
    private String id;
    private String content;
    private LocalDateTime timestamp;
    
    public Message() {
    }
    
    public Message(String id, String content, LocalDateTime timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    public static Message create(String content) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
}