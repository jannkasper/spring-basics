package com.kasper.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kasper.config.KafkaConfig;
import com.kasper.model.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;

    @KafkaListener(topics = KafkaConfig.CHAT_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ChatMessage message) {
        log.info("Received message from Kafka: {}", message);
        
        // Send to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
        
        // Save to MongoDB directly as a backup mechanism
        // This ensures messages are saved even if Kafka Connect is not set up yet or fails
        try {
            messageService.saveMessage(message);
        } catch (Exception e) {
            log.error("Failed to save message directly to MongoDB: {}", e.getMessage());
        }
    }
}