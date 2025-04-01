package com.kasper.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kasper.model.ChatMessage;
import com.kasper.service.ChatMessageProducer;
import com.kasper.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageProducer messageProducer;
    private final ChatMessageService messageService;

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
    
    // REST endpoints for message history
    
    @GetMapping("/rooms/{roomId}/messages")
    @ResponseBody
    public List<ChatMessage> getRoomMessages(@PathVariable String roomId) {
        return messageService.getMessagesByRoomId(roomId);
    }
    
    @GetMapping("/rooms/{roomId}/chat")
    @ResponseBody
    public List<ChatMessage> getRoomChatMessages(@PathVariable String roomId) {
        return messageService.getChatMessagesByRoomId(roomId);
    }
    
    @GetMapping("/users/{userId}/messages")
    @ResponseBody
    public List<ChatMessage> getUserMessages(@PathVariable String userId) {
        return messageService.getMessagesBySenderId(userId);
    }
}