package com.kasper.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.kasper.model.ChatMessage;
import com.kasper.service.ChatMessageProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageProducer messageProducer;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info("Received message to send: {}", chatMessage);
        
        // Add missing fields if not present
        if (chatMessage.getId() == null) {
            chatMessage.setId(UUID.randomUUID().toString());
        }
        
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        
        messageProducer.sendMessage(chatMessage);
    }

    @MessageMapping("/chat.join")
    public void join(@Payload ChatMessage chatMessage) {
        log.info("User joined: {}", chatMessage);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        messageProducer.sendMessage(chatMessage);
    }

    @MessageMapping("/chat.leave")
    public void leave(@Payload ChatMessage chatMessage) {
        log.info("User left: {}", chatMessage);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        messageProducer.sendMessage(chatMessage);
    }
}