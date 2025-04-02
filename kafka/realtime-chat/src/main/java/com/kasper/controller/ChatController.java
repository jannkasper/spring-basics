package com.kasper.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kasper.model.ChatMessage;
import com.kasper.service.ChatMessageProducer;
import com.kasper.service.ChatMessageService;
import com.kasper.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageProducer messageProducer;
    private final ChatMessageService messageService;
    private final UserStatusService userStatusService;
    private final SimpMessagingTemplate messagingTemplate;

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
        
        // Update user status
        userStatusService.updateUserStatus(chatMessage.getSenderId(), chatMessage.getRoomId(), true);
        
        // Broadcast updated user status to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatMessage.getRoomId() + "/users",
            userStatusService.getUsersInRoom(chatMessage.getRoomId())
        );
    }

    @MessageMapping("/chat.leave")
    public void leave(@Payload ChatMessage chatMessage) {
        log.info("User left: {}", chatMessage);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        messageProducer.sendMessage(chatMessage);
        
        // Update user status
        userStatusService.updateUserStatus(chatMessage.getSenderId(), chatMessage.getRoomId(), false);
        
        // Broadcast updated user status to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatMessage.getRoomId() + "/users",
            userStatusService.getUsersInRoom(chatMessage.getRoomId())
        );
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
    
    @MessageMapping("/chat.heartbeat")
    public void heartbeat(@Payload ChatMessage chatMessage) {
        // Update user's last seen timestamp
        userStatusService.updateUserStatus(chatMessage.getSenderId(), chatMessage.getRoomId(), true);
    }
}