package de.senatov.spring.kafka.pandcdemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaModel implements Serializable {

    private static final long serialVersionUID = 1671862795996898048L;

    private String field1;
    private String field2;
}
