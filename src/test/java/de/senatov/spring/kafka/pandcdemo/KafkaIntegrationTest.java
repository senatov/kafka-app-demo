package de.senatov.spring.kafka.pandcdemo;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test using Docker Compose.
 * Starts Kafka container before tests, stops after.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationTest {

    private static final String COMPOSE_FILE = "compose-test.yaml";
    private static final int KAFKA_PORT = 19092;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:" + KAFKA_PORT);
        registry.add("spring.docker.compose.enabled", () -> "false");
    }

    @BeforeAll
    void startKafka() throws IOException, InterruptedException {
        System.out.println("=== Cleaning up old containers ===");
        runCommand("docker", "compose", "-f", COMPOSE_FILE, "down", "-v");
        
        System.out.println("=== Starting Kafka container ===");
        runCommand("docker", "compose", "-f", COMPOSE_FILE, "up", "-d");
        
        System.out.println("=== Waiting for Kafka port to be available ===");
        waitForPort("localhost", KAFKA_PORT, 60);
        
        // Extra wait for Kafka to fully initialize
        System.out.println("=== Kafka port is open, waiting for broker to stabilize ===");
        Thread.sleep(5000);
        
        System.out.println("=== Kafka is ready ===");
    }

    @AfterAll
    void stopKafka() throws IOException, InterruptedException {
        // Give Spring time to close connections gracefully
        System.out.println("=== Waiting for Spring to shutdown gracefully ===");
        Thread.sleep(3000);
        
        System.out.println("=== Stopping and removing Kafka container ===");
        runCommand("docker", "compose", "-f", COMPOSE_FILE, "down", "-v");
    }

    private void runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new java.io.File(System.getProperty("user.dir")));
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor(120, TimeUnit.SECONDS);
    }

    private void waitForPort(String host, int port, int timeoutSeconds) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            try (Socket socket = new Socket(host, port)) {
                return; // Port is available
            } catch (IOException e) {
                System.out.println("Waiting for " + host + ":" + port + "...");
                Thread.sleep(1000);
            }
        }
        throw new RuntimeException("Timeout waiting for " + host + ":" + port);
    }

    @Test
    void shouldSendMessage() throws InterruptedException {
        Thread.sleep(2000); // Small buffer for connection
        
        KafkaModel message = KafkaModel.builder()
                .field1("test-field-1")
                .field2("test-field-2")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/kafka",
                message,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Message sent to Kafka topic");
    }

    @Test
    void healthCheckShouldReturnOk() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/kafka/health",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Kafka Producer is running");
    }
}
