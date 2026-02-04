package de.senatov.spring.kafka.pandcdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(KafkaModel message) {
        log.info("Received message: field1={}, field2={}", message.getField1(), message.getField2());
    }
}
