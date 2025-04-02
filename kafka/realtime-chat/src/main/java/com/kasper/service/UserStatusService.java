package com.kasper.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kasper.model.UserStatus;
import com.kasper.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    
    public void updateUserStatus(String userId, String roomId, boolean online) {
        UserStatus status = userStatusRepository.findById(userId)
                .orElse(new UserStatus());
        
        status.setUserId(userId);
        status.setRoomId(roomId);
        status.setOnline(online);
        status.setLastSeen(LocalDateTime.now());
        
        userStatusRepository.save(status);
        
        log.info("Updated user status: {}", status);
    }
    
    public List<UserStatus> getUsersInRoom(String roomId) {
        return userStatusRepository.findByRoomId(roomId);
    }
    
    public List<UserStatus> getOnlineUsers() {
        return userStatusRepository.findByOnlineTrue();
    }
    
    public UserStatus getUserStatus(String userId) {
        return userStatusRepository.findById(userId).orElse(null);
    }
}
