package com.oficina.billingservice.infrastructure.event;

import com.oficina.billingservice.domain.entity.Orcamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpBillingEventPublisher implements BillingEventPublisher {

    @Override
    public void publish(String routingKey, Orcamento orcamento) {
        log.debug("Mensageria desativada - evento local {} orçamento {}", routingKey, orcamento.getId());
    }
}
