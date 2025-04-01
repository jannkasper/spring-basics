package com.kasper.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kasper.model.ChatMessage;
import com.kasper.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    
    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        log.info("Retrieving messages for room: {}", roomId);
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
    
    public List<ChatMessage> getChatMessagesByRoomId(String roomId) {
        log.info("Retrieving chat messages for room: {}", roomId);
        return messageRepository.findByRoomIdAndTypeOrderByTimestampAsc(roomId, ChatMessage.MessageType.CHAT);
    }
    
    public List<ChatMessage> getMessagesBySenderId(String senderId) {
        log.info("Retrieving messages for user: {}", senderId);
        return messageRepository.findBySenderIdOrderByTimestampDesc(senderId);
    }
    
    public ChatMessage saveMessage(ChatMessage message) {
        message.setPersisted(true);
        return messageRepository.save(message);
    }
}