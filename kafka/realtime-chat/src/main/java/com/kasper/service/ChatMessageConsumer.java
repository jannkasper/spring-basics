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

    @KafkaListener(topics = KafkaConfig.CHAT_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ChatMessage message) {
        log.info("Received message from Kafka: {}", message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }
}