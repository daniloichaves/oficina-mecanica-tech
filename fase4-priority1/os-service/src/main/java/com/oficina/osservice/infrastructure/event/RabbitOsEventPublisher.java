package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.domain.entity.StatusOrdemServico;
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
public class RabbitOsEventPublisher implements OsEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public RabbitOsEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.exchange:oficina.events}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publishStatusChanged(String ordemId, StatusOrdemServico status) {
        String routingKey = status == StatusOrdemServico.RECEBIDA ? "os.criada" : "os.status.atualizado";
        OsEventMessage payload = new OsEventMessage(
                Long.valueOf(ordemId),
                status,
                routingKey,
                MDC.get("correlationId"),
                Instant.now()
        );
        log.info("Publicando evento {} para OS {} no exchange {}", routingKey, ordemId, exchange);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
