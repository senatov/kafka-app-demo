package de.senatov.spring.kafka.pandcdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaTemplate<String, KafkaModel> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topicName;

    @PostMapping
    public ResponseEntity<String> post(@RequestBody KafkaModel kafkaModel) {
        log.info("Sending message to Kafka: {}", kafkaModel);

        CompletableFuture<SendResult<String, KafkaModel>> future =
                kafkaTemplate.send(topicName, kafkaModel);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to partition {} with offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message", ex);
            }
        });

        return ResponseEntity.ok("Message sent to Kafka topic: " + topicName);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kafka Producer is running");
    }
}
