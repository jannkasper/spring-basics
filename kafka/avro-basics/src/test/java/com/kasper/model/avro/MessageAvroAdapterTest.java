package com.kasper.model.avro;

import com.kasper.model.Message;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageAvroAdapterTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void shouldConvertToAvro() {
        // Given
        String id = UUID.randomUUID().toString();
        String content = "Test content";
        LocalDateTime timestamp = LocalDateTime.now();
        Message message = new Message(id, content, timestamp);
        
        // When
        MessageAvro avroMessage = MessageAvroAdapter.toAvro(message);
        
        // Then
        assertThat(avroMessage).isNotNull();
        assertThat(avroMessage.getId()).isEqualTo(id);
        assertThat(avroMessage.getContent()).isEqualTo(content);
        assertThat(avroMessage.getTimestamp()).isEqualTo(timestamp.format(formatter));
    }
    
    @Test
    void shouldConvertFromAvro() {
        // Given
        String id = UUID.randomUUID().toString();
        String content = "Test content";
        LocalDateTime timestamp = LocalDateTime.now();
        String formattedTimestamp = timestamp.format(formatter);
        
        MessageAvro avroMessage = MessageAvro.newBuilder()
                .setId(id)
                .setContent(content)
                .setTimestamp(formattedTimestamp)
                .build();
        
        // When
        Message message = MessageAvroAdapter.fromAvro(avroMessage);
        
        // Then
        assertThat(message).isNotNull();
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.getTimestamp()).isEqualTo(timestamp);
    }
    
    @Test
    void shouldHandleNullMessage() {
        // When
        MessageAvro avroMessage = MessageAvroAdapter.toAvro(null);
        Message message = MessageAvroAdapter.fromAvro(null);
        
        // Then
        assertThat(avroMessage).isNull();
        assertThat(message).isNull();
    }
}