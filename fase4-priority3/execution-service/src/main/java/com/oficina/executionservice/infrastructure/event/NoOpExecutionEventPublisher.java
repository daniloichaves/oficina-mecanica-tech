package com.oficina.executionservice.infrastructure.event;

import com.oficina.executionservice.domain.entity.Execucao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpExecutionEventPublisher implements ExecutionEventPublisher {

    @Override
    public void publish(String routingKey, Execucao execucao) {
        log.debug("Mensageria desativada - evento local {} execução {}", routingKey, execucao.getId());
    }
}
