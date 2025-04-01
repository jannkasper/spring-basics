package com.kasper.producer;

import com.kasper.config.KafkaConfig;
import com.kasper.model.Message;
import com.kasper.model.avro.MessageAvro;
import com.kasper.model.avro.MessageAvroAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private KafkaTemplate<String, MessageAvro> kafkaTemplate;
    
    @Mock
    private SendResult<String, MessageAvro> sendResult;
    
    private MessageProducer messageProducer;
    
    @BeforeEach
    void setUp() {
        messageProducer = new MessageProducer(kafkaTemplate);
    }
    
    @Test
    void shouldSendMessageToKafka() {
        // Given
        String id = UUID.randomUUID().toString();
        String content = "Test message";
        LocalDateTime timestamp = LocalDateTime.now();
        Message message = new Message(id, content, timestamp);
        
        MessageAvro expectedAvroMessage = MessageAvroAdapter.toAvro(message);
        
        CompletableFuture<SendResult<String, MessageAvro>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any(MessageAvro.class))).thenReturn(future);
        
        // When
        CompletableFuture<SendResult<String, MessageAvro>> result = messageProducer.sendMessage(message);
        
        // Then
        ArgumentCaptor<MessageAvro> avroCaptor = ArgumentCaptor.forClass(MessageAvro.class);
        verify(kafkaTemplate).send(eq(KafkaConfig.TOPIC_NAME), eq(id), avroCaptor.capture());
        
        MessageAvro capturedAvro = avroCaptor.getValue();
        assertThat(capturedAvro.getId()).isEqualTo(id);
        assertThat(capturedAvro.getContent()).isEqualTo(content);
        
        // Verify the result is the expected future
        assertThat(result).isEqualTo(future);
    }
}