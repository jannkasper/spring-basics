package com.kasper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.kasper.config.KafkaConfig;
import com.kasper.model.Message;
import com.kasper.producer.MessageProducer;
import com.kasper.serialization.MessageDeserializer;
import com.kasper.serialization.MessageSerializer;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = { KafkaConfig.TOPIC_NAME })
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
public class KafkaMessagingIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    
    @Autowired
    private MessageProducer producer;

    @Test
    public void testSendReceiveMessage() throws Exception {
        // Create a test message
        Message testMessage = Message.create("Test message content");

        // Send the message and wait for completion
        producer.sendMessage(testMessage).get(10, TimeUnit.SECONDS);

        // Create a consumer for verification
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        DefaultKafkaConsumerFactory<String, Message> cf = new DefaultKafkaConsumerFactory<>(consumerProps, 
                new StringDeserializer(), new MessageDeserializer());
        Consumer<String, Message> consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, KafkaConfig.TOPIC_NAME);

        // Wait for and verify the message
        AtomicReference<Message> receivedMessage = new AtomicReference<>();
        
        await()
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(1))
            .untilAsserted(() -> {
                ConsumerRecords<String, Message> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
                boolean messageFound = false;
                
                for (ConsumerRecord<String, Message> record : records) {
                    if (record.value() != null && 
                        testMessage.getId().equals(record.key())) {
                        
                        receivedMessage.set(record.value());
                        messageFound = true;
                        break;
                    }
                }
                
                assertThat(messageFound).isTrue();
                assertThat(receivedMessage.get()).isNotNull();
                assertThat(receivedMessage.get().getId()).isEqualTo(testMessage.getId());
            });
        
        // Verify message content
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getId()).isEqualTo(testMessage.getId());
        assertThat(receivedMessage.get().getContent()).isEqualTo(testMessage.getContent());
        
        // Clean up
        consumer.close();
    }
    
    @Configuration
    static class TestConfig {
        
        @Bean
        public KafkaTemplate<String, Message> testKafkaTemplate(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> producerProps = new HashMap<>(
                    KafkaTestUtils.producerProps(embeddedKafkaBroker));
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class);
            
            DefaultKafkaProducerFactory<String, Message> producerFactory = 
                    new DefaultKafkaProducerFactory<>(producerProps);
            
            return new KafkaTemplate<>(producerFactory);
        }
        
        @Bean
        public MessageProducer messageProducer(KafkaTemplate<String, Message> kafkaTemplate) {
            return new MessageProducer(kafkaTemplate);
        }
    }
}