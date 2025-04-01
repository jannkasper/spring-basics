package com.kasper.model.avro;

import com.kasper.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageAvroAdapter {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    
    public static MessageAvro toAvro(Message message) {
        if (message == null) {
            return null;
        }
        
        return MessageAvro.newBuilder()
                .setId(message.getId())
                .setContent(message.getContent())
                .setTimestamp(message.getTimestamp().format(formatter))
                .build();
    }
    
    public static Message fromAvro(MessageAvro avroMessage) {
        if (avroMessage == null) {
            return null;
        }
        
        Message message = new Message();
        message.setId(avroMessage.getId());
        message.setContent(avroMessage.getContent());
        message.setTimestamp(LocalDateTime.parse(avroMessage.getTimestamp(), formatter));
        return message;
    }
}