package com.oficina.billingservice.infrastructure.event;

import com.oficina.billingservice.domain.entity.Orcamento;
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
public class RabbitBillingEventPublisher implements BillingEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public RabbitBillingEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.exchange:oficina.events}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publish(String routingKey, Orcamento orcamento) {
        BillingEventMessage msg = new BillingEventMessage(
                orcamento.getId(),
                orcamento.getOrdemId(),
                orcamento.getStatus(),
                orcamento.getValor(),
                routingKey,
                MDC.get("correlationId"),
                Instant.now()
        );
        log.info("Publicando evento {} para orçamento {} (OS {})", routingKey, orcamento.getId(), orcamento.getOrdemId());
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }
}
