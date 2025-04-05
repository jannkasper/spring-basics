# Real-time Log Monitoring System

A proof of concept for a real-time log monitoring system based on event-driven Kafka architecture, leveraging Spring Boot microservices.

## Architecture

The system consists of the following components:

1. **Discovery Service**: Eureka server for service registration and discovery
2. **Log Producer Service**: Generates and publishes log events to Kafka
3. **Log Consumer Service**: Consumes and processes log events from Kafka
4. **Kafka Infrastructure**: Message broker for handling log events

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker and Docker Compose

### Running the Application

1. Start the Kafka infrastructure:

```bash
docker-compose up -d
```

2. Start the Discovery Service:

```bash
cd discovery-service
mvn spring-boot:run
```

3. Start the Log Producer Service:

```bash
cd log-producer-service
mvn spring-boot:run
```

4. Start the Log Consumer Service:

```bash
cd log-consumer-service
mvn spring-boot:run
```

## Services

### Discovery Service (Port: 8761)

The Eureka server for service discovery. Access the dashboard at: http://localhost:8761

### Log Producer Service (Port: 8081)

This service generates random log events and publishes them to the Kafka topic `app-logs`. It also exposes a REST API to manually send log events.

**API Endpoints**:

- `POST /logs` - Publish a custom log event

Example request:

```json
{
  "level": "ERROR",
  "service": "user-service",
  "message": "Null pointer exception occurred"
}
```

### Log Consumer Service (Port: 8082)

This service consumes log events from the Kafka topic and provides endpoints to access and query the logs.

**API Endpoints**:

- `GET /logs` - Get all recent logs
- `GET /logs/service/{serviceName}` - Get logs filtered by service name
- `GET /logs/level/{level}` - Get logs filtered by level (INFO, WARN, ERROR)
- `GET /logs/stats/services` - Get log count statistics by service
- `GET /logs/stats/levels` - Get log count statistics by level

## Log Format

The system uses a standardized JSON format for log events:

```json
{
  "timestamp": "2025-04-05T10:23:45Z",
  "level": "ERROR",
  "service": "user-service",
  "message": "Null pointer exception occurred"
}
```

## Future Enhancements

- Add authentication and authorization
- Implement persistent storage (MongoDB, Elasticsearch)
- Create a real-time web dashboard
- Add alerting capabilities for critical log events
- Implement log aggregation and analysis