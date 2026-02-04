# Kafka Demo Application

Spring Boot 3.4 + Apache Kafka demo application with auto-start Docker containers.

## Requirements

- Java 21+
- Maven 3.9+
- Docker Desktop (for Kafka containers)

## Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run (Docker Compose starts automatically)
mvn spring-boot:run
```

## Features

- **Spring Boot 3.4.2** with Java 21
- **Apache Kafka 3.9** (KRaft mode, no Zookeeper)
- **Auto-start Docker containers** via `spring-boot-docker-compose`
- **Kafka UI** at http://localhost:8080
- **REST API** at http://localhost:8081
- **Testcontainers** for integration tests

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/kafka` | Send message to Kafka |
| GET | `/api/kafka/health` | Health check |
| GET | `/actuator/health` | Actuator health |

## Send Test Message

```bash
curl -X POST http://localhost:8081/api/kafka \
  -H "Content-Type: application/json" \
  -d '{"field1":"hello","field2":"world"}'
```

## Project Structure

```
├── compose.yaml              # Docker Compose (auto-started)
├── pom.xml                   # Maven config
└── src/
    └── main/
        ├── java/.../pandcdemo/
        │   ├── PAndCDemoApplication.java
        │   ├── KafkaConfig.java
        │   ├── KafkaController.java
        │   ├── KafkaConsumer.java
        │   └── KafkaModel.java
        └── resources/
            └── application.properties
```

## Run Tests

```bash
mvn test
```

Tests use Testcontainers (separate from compose.yaml).
