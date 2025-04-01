package com.kasper.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.kasper.config.KafkaConfig;
import com.kasper.model.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void sendMessage(ChatMessage message) {
        log.info("Sending message to Kafka: {}", message);
        kafkaTemplate.send(KafkaConfig.CHAT_TOPIC, message.getRoomId(), message);
    }
}