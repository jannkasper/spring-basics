package com.kasper.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasper.model.UserStatus;
import com.kasper.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/status")
@Slf4j
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/users/online")
    public List<UserStatus> getOnlineUsers() {
        return userStatusService.getOnlineUsers();
    }
    
    @GetMapping("/rooms/{roomId}/users")
    public List<UserStatus> getUsersInRoom(@PathVariable String roomId) {
        return userStatusService.getUsersInRoom(roomId);
    }
    
    @GetMapping("/users/{userId}")
    public UserStatus getUserStatus(@PathVariable String userId) {
        return userStatusService.getUserStatus(userId);
    }
    
    @MessageMapping("/status.connect/{roomId}")
    public void connectUser(@DestinationVariable String roomId, String userId) {
        log.info("User connected: {} in room: {}", userId, roomId);
        
        userStatusService.updateUserStatus(userId, roomId, true);
        
        // Broadcast updated user list to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId + "/users",
            userStatusService.getUsersInRoom(roomId)
        );
    }
    
    @MessageMapping("/status.disconnect/{roomId}")
    public void disconnectUser(@DestinationVariable String roomId, String userId) {
        log.info("User disconnected: {} in room: {}", userId, roomId);
        
        userStatusService.updateUserStatus(userId, roomId, false);
        
        // Broadcast updated user list to room
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId + "/users",
            userStatusService.getUsersInRoom(roomId)
        );
    }
}