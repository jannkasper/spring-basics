# Kafka Avro Messaging Demo

This Spring Boot application demonstrates how to use Apache Kafka with Avro schema serialization for messaging. It's designed as a learning resource for beginners who want to understand the basics of Kafka, Avro, and how they integrate in a Spring Boot application.

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Key Concepts](#key-concepts)

## Overview

This application implements a simple producer-consumer messaging pattern using Apache Kafka as the message broker and Apache Avro for data serialization. The app sends and receives messages with a standardized schema, demonstrating how to implement schema-based messaging in a Spring Boot application.

## Technologies Used

- **Java 17**
- **Spring Boot 3.3.10**
- **Apache Kafka** - Distributed streaming platform
- **Apache Avro** - Data serialization system
- **Confluent Schema Registry** - Manages Avro schemas
- **Spring Kafka** - Integration of Spring with Kafka
- **JUnit 5** - Testing framework
- **EmbeddedKafka** - For testing Kafka functionality
- **MockSchemaRegistry** - For testing with Schema Registry
- **Awaitility** - Testing asynchronous operations

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── kasper/
│   │           ├── AvroBasicsApplication.java
│   │           ├── config/
│   │           │   └── KafkaConfig.java
│   │           ├── consumer/
│   │           │   └── MessageConsumer.java
│   │           ├── model/
│   │           │   ├── Message.java
│   │           │   └── avro/
│   │           │       ├── MessageAvro.java
│   │           │       └── MessageAvroAdapter.java
│   │           └── producer/
│   │               └── MessageProducer.java
│   └── resources/
│       ├── application.properties
│       └── avro/
│           └── message.avsc
└── test/
    └── java/
        └── com/
            └── kasper/
                ├── AvroBasicsApplicationTests.java
                ├── KafkaAvroMessagingIntegrationTest.java
                ├── config/
                │   └── TestKafkaConfig.java
                ├── model/
                │   └── avro/
                │       └── MessageAvroAdapterTest.java
                └── producer/
                    └── MessageProducerTest.java
```

## How It Works

1. **Avro Schema Definition**: The application defines a schema for messages in `message.avsc`.

2. **Message Models**:
   - `Message.java` - Domain model class
   - `MessageAvro.java` - Auto-generated class from the Avro schema
   - `MessageAvroAdapter.java` - Converts between domain model and Avro model

3. **Producer**: `MessageProducer.java` sends messages to a Kafka topic, converting the domain model to Avro format.

4. **Consumer**: `MessageConsumer.java` listens to the Kafka topic, receives Avro messages, and converts them back to the domain model.

5. **Configuration**: `KafkaConfig.java` sets up the Kafka topics and components.

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose (for running Kafka, Zookeeper, and Schema Registry)

### Running the Application

1. **Start the Kafka infrastructure**:

   ```bash
   docker-compose up -d
   ```

   This starts Zookeeper, Kafka, and Schema Registry.

2. **Run the Spring Boot application**:

   ```bash
   ./mvnw spring-boot:run
   ```

## Testing

The application includes several tests:

1. **Unit Tests**:
   - `MessageAvroAdapterTest.java` - Tests conversion between domain and Avro models
   - `MessageProducerTest.java` - Tests the message producer in isolation

2. **Integration Tests**:
   - `KafkaAvroMessagingIntegrationTest.java` - Tests the end-to-end flow using embedded Kafka and mock Schema Registry

Run all tests with:

```bash
./mvnw test
```

## Key Concepts

### Apache Kafka

Kafka is a distributed streaming platform that lets you:
- Publish and subscribe to streams of records
- Store streams of records in a fault-tolerant way
- Process streams of records as they occur

### Apache Avro

Avro is a data serialization system that provides:
- Rich data structures
- A compact, fast binary data format
- Schema definition in JSON
- Code generation from schemas
- Compatibility with schema evolution

### Schema Registry

The Schema Registry:
- Stores Avro schemas
- Enforces schema compatibility rules
- Allows for schema evolution over time
- Reduces message size by sending schema IDs instead of full schemas

### Kafka-Avro Integration

When using Kafka with Avro:
1. The producer serializes data using Avro and the Schema Registry
2. Serialized data is sent to Kafka
3. The consumer deserializes the data using the same schema from the Registry

This provides type safety and efficient serialization while allowing for schema evolution.

### Spring Kafka

Spring Kafka provides:
- `KafkaTemplate` for producing messages
- `@KafkaListener` annotation for consuming messages
- Integration with Spring's dependency injection and configuration

## Avro Schema Example

```json
{
  "namespace": "com.kasper.model.avro",
  "type": "record",
  "name": "MessageAvro",
  "fields": [
    {"name": "id", "type": "string"},
    {"name": "content", "type": "string"},
    {"name": "timestamp", "type": "string"}
  ]
}
```

## Spring Configuration Example

```properties
# Kafka Producer Configuration
spring.kafka.producer.bootstrap-servers=localhost:29092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=io.confluent.kafka.serializers.KafkaAvroSerializer

# Kafka Consumer Configuration
spring.kafka.consumer.bootstrap-servers=localhost:29092
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=io.confluent.kafka.serializers.KafkaAvroDeserializer
spring.kafka.consumer.group-id=message-consumer-group
```