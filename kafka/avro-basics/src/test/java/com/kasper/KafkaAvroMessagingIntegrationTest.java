package com.kasper;

import com.kasper.config.KafkaConfig;
import com.kasper.model.Message;
import com.kasper.model.avro.MessageAvro;
import com.kasper.producer.MessageProducer;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.properties.schema.registry.url=mock://test-schema-registry",
        "spring.kafka.producer.properties.schema.registry.url=mock://test-schema-registry",
        "spring.kafka.consumer.properties.schema.registry.url=mock://test-schema-registry",
        "spring.kafka.producer.properties.auto.register.schemas=true",
        "spring.kafka.consumer.properties.specific.avro.reader=true"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {KafkaConfig.TOPIC_NAME})
public class KafkaAvroMessagingIntegrationTest {

    private static final String MOCK_SCHEMA_REGISTRY_URL = "mock://test-schema-registry";
    
    @Autowired
    private MessageProducer messageProducer;
    
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    
    private Consumer<String, MessageAvro> consumer;
    private KafkaTemplate<String, MessageAvro> kafkaTemplate;

    @BeforeEach
    void setUp() {
        // Set up the consumer for test verification
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-consumer-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);
        consumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        
        DefaultKafkaConsumerFactory<String, MessageAvro> consumerFactory = 
                new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = consumerFactory.createConsumer("test-consumer", "1");
        consumer.subscribe(Collections.singletonList(KafkaConfig.TOPIC_NAME));
        
        // Set up the producer for direct testing
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);
        
        DefaultKafkaProducerFactory<String, MessageAvro> producerFactory = 
                new DefaultKafkaProducerFactory<>(producerProps);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }
    
    @AfterEach
    void tearDown() {
        // Close the consumer and clean up the schema registry
        consumer.close();
        MockSchemaRegistry.dropScope(MOCK_SCHEMA_REGISTRY_URL);
    }
    
    @Test
    void testProducerSendsMessageSuccessfully() {
        // Create a test message
        String id = UUID.randomUUID().toString();
        Message message = new Message(id, "Test message content", LocalDateTime.now());
        
        // Send the message through our producer
        messageProducer.sendMessage(message);
        
        // Verify the message was received correctly
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, MessageAvro> records = KafkaTestUtils.getRecords(consumer, Duration.ofMillis(10000), 10);
            assertThat(records.count()).isGreaterThan(0);
            
            boolean foundRecord = false;
            for (var record : records) {
                if (id.equals(record.key())) {
                    foundRecord = true;
                    assertThat(record.value()).isNotNull();
                    assertThat(record.value().getId()).isEqualTo(id);
                    assertThat(record.value().getContent()).isEqualTo("Test message content");
                    break;
                }
            }
            
            assertThat(foundRecord).isTrue();
        });
    }
    
    @Test
    void testConsumerProcessesMessageCorrectly(CapturedOutput output) {
        // Create an Avro message to publish directly
        String id = UUID.randomUUID().toString();
        String content = "Direct test message";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        
        MessageAvro avroMessage = MessageAvro.newBuilder()
                .setId(id)
                .setContent(content)
                .setTimestamp(timestamp)
                .build();
        
        // Send message directly to topic
        kafkaTemplate.send(KafkaConfig.TOPIC_NAME, id, avroMessage).whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed to send test message: " + ex.getMessage());
            }
        });
        
        // Verify that our consumer logs the message
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(output.toString()).contains("Received message");
            assertThat(output.toString()).contains(id);
            assertThat(output.toString()).contains(content);
        });
    }
    
    @Test
    void testEndToEndMessaging(CapturedOutput output) {
        // Create a test message
        String id = UUID.randomUUID().toString();
        String content = "End-to-end test message";
        Message message = new Message(id, content, LocalDateTime.now());
        
        // Send the message through our producer
        messageProducer.sendMessage(message).whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed to send test message: " + ex.getMessage());
            }
        });
        
        // Verify that our consumer logs the message
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(output.toString()).contains("Received message");
            assertThat(output.toString()).contains(id);
            assertThat(output.toString()).contains(content);
            
            // Also verify acknowledgment
            assertThat(output.toString()).contains("Message acknowledged: " + id);
        });
    }
}