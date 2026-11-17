package com.oficina.executionservice.infrastructure.event;

import com.oficina.executionservice.domain.entity.Execucao;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class RabbitExecutionEventPublisher implements ExecutionEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public RabbitExecutionEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.exchange:oficina.events}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publish(String routingKey, Execucao execucao) {
        ExecutionEventMessage msg = new ExecutionEventMessage(
                execucao.getId(),
                execucao.getOrdemId(),
                execucao.getStatus(),
                routingKey,
                MDC.get("correlationId"),
                Instant.now()
        );
        log.info("Publicando evento {} para execução {} (OS {})", routingKey, execucao.getId(), execucao.getOrdemId());
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }
}
