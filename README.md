# Kafka Demo Application

Spring Boot 3.4 приложение с Apache Kafka, демонстрирующее Producer/Consumer паттерн.

## Требования

- Java 21+
- Docker Desktop (для Kafka)

## Быстрый старт

```bash
# Запуск приложения (автоматически поднимает Kafka через Docker Compose)
./gradlew bootRun

# Или сборка и запуск JAR
./gradlew bootJar
java -jar build/libs/kafka-demo-1.0.0-SNAPSHOT.jar
```

## Gradle команды

| Команда | Описание |
|---------|----------|
| `./gradlew bootRun` | Запуск приложения |
| `./gradlew build` | Полная сборка + тесты |
| `./gradlew build -x test` | Сборка без тестов |
| `./gradlew test` | Запуск тестов |
| `./gradlew clean` | Очистка build/ |
| `./gradlew dependencies` | Показать дерево зависимостей |
| `./gradlew bootJar` | Собрать executable JAR |

## API Endpoints

```bash
# Отправить сообщение в Kafka
curl -X POST http://localhost:8081/api/kafka \
  -H "Content-Type: application/json" \
  -d '{"field1":"hello","field2":"world"}'

# Health check
curl http://localhost:8081/api/kafka/health

# Actuator
curl http://localhost:8081/actuator/health
```

## Порты

| Сервис | Порт |
|--------|------|
| Application | 8081 |
| Kafka Broker | 9092 |
| Kafka UI | 8080 |

## Структура проекта

```
kafka-app-demo/
├── build.gradle.kts          # Gradle build (Kotlin DSL)
├── settings.gradle.kts       # Gradle settings
├── compose.yaml              # Docker Compose (Kafka + UI)
├── gradlew                   # Gradle Wrapper (Unix)
├── gradlew.bat               # Gradle Wrapper (Windows)
└── src/
    ├── main/
    │   ├── java/             # Java sources
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/             # Integration tests (Testcontainers)
```

## Технологии

- Spring Boot 3.4.2
- Apache Kafka 3.9 (KRaft mode)
- Testcontainers 2.0.3
- Gradle 9.3.1 (Kotlin DSL)
- Java 21
