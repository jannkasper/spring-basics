package com.kasper.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_NAME = "messages";
    
    @Bean
    public NewTopic messagesTopic() {
        return TopicBuilder.name(TOPIC_NAME)
                .partitions(1)
                .replicas(1)
                .build();
    }
}