package com.kasper.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.kasper.model.Message;

@Service
@ConditionalOnProperty(value = "spring.kafka.listener.auto-startup", havingValue = "true", matchIfMissing = true)
public class MessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(
            @Payload Message message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received message: {}", message);
            logger.info("Topic: {}, Partition: {}, Key: {}, Offset: {}", topic, partition, key, offset);
            
            // Process the message (real-world implementation would do something with it)
            processMessage(message);
            
            // Acknowledge the message after successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                logger.info("Message acknowledged: {}", message.getId());
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", message.getId(), e);
            // In a real application, you might have retry logic here
            // For critical errors, you might still acknowledge to move on
            // or implement a dead letter queue
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
    
    private void processMessage(Message message) {
        // In a real application, you would process the message here
        logger.info("Processing message with ID: {}", message.getId());
        
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}