package com.kasper.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kasper.model.Message;

public class MessageDeserializer implements Deserializer<Message> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public MessageDeserializer() {
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                logger.warn("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(data, Message.class);
        } catch (Exception e) {
            logger.error("Error when deserializing byte[] to Message", e);
            throw new SerializationException("Error when deserializing byte[] to Message", e);
        }
    }
}