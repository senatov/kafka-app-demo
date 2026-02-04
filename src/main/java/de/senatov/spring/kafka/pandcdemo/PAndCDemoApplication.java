package de.senatov.spring.kafka.pandcdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PAndCDemoApplication {

    public static void main(String[] args) {
        log.info("Starting Kafka Demo Application...");
        SpringApplication.run(PAndCDemoApplication.class, args);
    }
}
