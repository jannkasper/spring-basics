package com.kasper.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kasper.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
    
    List<ChatMessage> findByRoomIdAndTypeOrderByTimestampAsc(String roomId, ChatMessage.MessageType type);
    
    List<ChatMessage> findBySenderIdOrderByTimestampDesc(String senderId);
}