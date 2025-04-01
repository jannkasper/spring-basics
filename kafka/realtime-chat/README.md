# Real-time Chat Application with Spring Boot and Kafka

This is a real-time chat application built with Spring Boot, WebSockets, and Apache Kafka.

## Features

- Real-time messaging using WebSockets
- Message distribution via Kafka
- Multiple chat rooms support
- Join/Leave notifications

## Planned Improvements

- Message persistence using Kafka Connect
- Read receipts via Kafka Streams
- User presence detection (online/offline status)
- Message encryption for private chats

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose

## How to Run

1. Start Kafka and Zookeeper using Docker Compose:

```shell
docker-compose up -d
```

2. Build the application:

```shell
./mvnw clean package
```

3. Run the application:

```shell
./mvnw spring-boot:run
```

4. Access the application at http://localhost:8080

5. Access Kafka UI at http://localhost:8090

## How to Use

1. Open the application in your web browser
2. Enter your name and a room ID
3. Start chatting!
4. You can open multiple browser tabs to simulate different users

## Technical Details

- Spring Boot 3.3.10 for the backend
- Spring WebSocket for real-time communication
- Apache Kafka for message distribution
- Simple Bootstrap UI for the frontend
- Lombok for reducing boilerplate code

## Architecture

- WebSocket connections are established between clients and the server
- Messages are sent through the server to Kafka
- Kafka distributes messages to all server instances
- Server instances push messages to connected clients

This architecture allows for horizontal scaling of the chat application.