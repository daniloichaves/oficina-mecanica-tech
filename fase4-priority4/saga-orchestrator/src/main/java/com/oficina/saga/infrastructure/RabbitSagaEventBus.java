package com.oficina.saga.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class RabbitSagaEventBus implements SagaEventBus {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public RabbitSagaEventBus(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.exchange:oficina.events}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publish(String topic, String payload) {
        log.info("Publicando evento de saga {} no exchange {}: {}", topic, exchange, payload);
        rabbitTemplate.convertAndSend(exchange, topic, Map.of(
                "evento", topic,
                "payload", payload,
                "correlationId", String.valueOf(MDC.get("correlationId")),
                "timestamp", Instant.now().toString()
        ));
    }
}
