# Kafka Basics

A simple Spring Boot application demonstrating Kafka Producer and Consumer implementation.

## Features

- JSON message serialization and deserialization
- Spring Kafka integration
- Docker Compose setup for local Kafka environment
- Integration testing with Embedded Kafka

## Message Structure

Messages are JSON objects with the following structure:

```json
{
  "id": "UUID string",
  "content": "Message content",
  "timestamp": "ISO date-time"
}
```

## Setup

### Prerequisites

- Java 17+
- Maven
- Docker and Docker Compose

### Running Kafka Locally

Start the Kafka environment using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- Zookeeper (port 2181)
- Kafka broker (port 9092)
- Kafka UI (port 8080)

### Running the Application

```bash
mvn spring-boot:run
```

The application will:
1. Connect to Kafka
2. Send 5 sample messages
3. Consume the messages and log them

### Testing

Run the integration tests with:

```bash
mvn test
```

## Usage

### Producer

The `MessageProducer` class is used to send messages to Kafka:

```java
@Autowired
private MessageProducer producer;

// Create and send a message
Message message = Message.create("Your message content");
producer.sendMessage(message);
```

### Consumer

The `MessageConsumer` class automatically consumes messages from the configured topic.

## Configuration

The application is configured through `application.properties`. Key settings:

- `spring.kafka.producer.*` - Producer configuration
- `spring.kafka.consumer.*` - Consumer configuration
- `spring.kafka.listener.*` - Kafka listener configuration

## Shutdown

To stop the Kafka environment:

```bash
docker-compose down
```