package com.kasper.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.kasper.config.KafkaConfig;
import com.kasper.model.Message;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    
    private final KafkaTemplate<String, Message> kafkaTemplate;
    
    @Autowired
    public MessageProducer(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public CompletableFuture<SendResult<String, Message>> sendMessage(Message message) {
        logger.info("Sending message: {}", message);
        
        CompletableFuture<SendResult<String, Message>> future = kafkaTemplate.send(
                KafkaConfig.TOPIC_NAME, message.getId(), message);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message sent successfully: [{}] partition: {}",
                        message, result.getRecordMetadata().partition());
            } else {
                logger.error("Unable to send message: [{}]", message, ex);
            }
        });
        
        return future;
    }
}