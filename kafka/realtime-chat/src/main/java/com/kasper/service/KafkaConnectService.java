package com.kasper.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kasper.config.KafkaConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConnectService {

    @Value("${kafka.connect.url}")
    private String kafkaConnectUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @EventListener(ApplicationReadyEvent.class)
    public void setupKafkaConnectSink() {
        try {
            log.info("Setting up MongoDB Sink connector for chat messages...");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> connectorConfig = new HashMap<>();
            connectorConfig.put("name", "chat-messages-sink");
            connectorConfig.put("config", createMongoSinkConfig());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(connectorConfig, headers);
            
            // Check if connector already exists, if not create it
            try {
                restTemplate.getForObject(kafkaConnectUrl + "/connectors/chat-messages-sink", Object.class);
                log.info("MongoDB Sink connector already exists");
            } catch (Exception e) {
                log.info("Creating MongoDB Sink connector");
                restTemplate.postForObject(kafkaConnectUrl + "/connectors", request, Object.class);
                log.info("MongoDB Sink connector created successfully");
            }
        } catch (Exception e) {
            log.error("Failed to set up MongoDB Sink connector", e);
        }
    }
    
    private Map<String, String> createMongoSinkConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("connector.class", "com.mongodb.kafka.connect.MongoSinkConnector");
        config.put("tasks.max", "1");
        config.put("topics", KafkaConfig.CHAT_TOPIC);
        config.put("connection.uri", "mongodb://mongodb:27017");
        config.put("database", "chat_db");
        config.put("collection", "messages");
        config.put("key.converter", "org.apache.kafka.connect.storage.StringConverter");
        config.put("value.converter", "org.apache.kafka.connect.json.JsonConverter");
        config.put("value.converter.schemas.enable", "false");
        config.put("document.id.strategy", "com.mongodb.kafka.connect.sink.processor.id.strategy.UuidStrategy");
        config.put("transforms", "TimestampConverter");
        config.put("transforms.TimestampConverter.type", "org.apache.kafka.connect.transforms.TimestampConverter$Value");
        config.put("transforms.TimestampConverter.field", "timestamp");
        config.put("transforms.TimestampConverter.target.type", "unix");
        return config;
    }
}