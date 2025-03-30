package com.kasper.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kasper.model.Message;

public class MessageSerializer implements Serializer<Message> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public MessageSerializer() {
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            if (data == null) {
                logger.warn("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error("Error when serializing Message to byte[]", e);
            throw new SerializationException("Error when serializing Message to byte[]", e);
        }
    }
}