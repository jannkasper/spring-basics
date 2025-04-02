# Event-Driven To-Do List Application with CQRS

This is a simple To-Do List application implementing the CQRS (Command Query Responsibility Segregation) pattern using Spring Boot and Apache Kafka for event processing.

## Architecture Overview

The application uses the CQRS pattern:

- **Command Side**: Handles create, update, delete operations
- **Query Side**: Handles read operations from a dedicated read model

Events are published to Kafka topics and consumed by event handlers to update the read model.

## Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose

## Running the Application

### Build the Application

```bash
mvn clean package
```

### Start the Infrastructure (Kafka)

```bash
docker-compose up -d zookeeper kafka
```

### Start the Application

#### Option 1: Run locally

```bash
java -jar target/event-driven-0.0.1-SNAPSHOT.jar
```

#### Option 2: Run with Docker Compose

```bash
docker-compose up -d
```

## API Endpoints

### Command API

- **Create Todo**: `POST /api/todos/commands`
  ```json
  {
    "title": "Buy groceries",
    "description": "Milk, eggs, bread"
  }
  ```

- **Update Todo**: `PUT /api/todos/commands/{id}`
  ```json
  {
    "title": "Buy groceries",
    "description": "Milk, eggs, bread, cheese",
    "completed": true
  }
  ```

- **Delete Todo**: `DELETE /api/todos/commands/{id}`

### Query API

- **Get All Todos**: `GET /api/todos`
- **Get Todo by ID**: `GET /api/todos/{id}`
- **Get Todos by Status**: `GET /api/todos/status/{completed}`

## CQRS and Event Sourcing Pattern

This application demonstrates:

1. **Command-Query Separation**: Writing and reading operations are separated
2. **Event-Driven Communication**: Commands generate events that update the read model
3. **Eventual Consistency**: The read model is updated asynchronously

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring for Apache Kafka
- H2 Database
- Docker & Docker Compose