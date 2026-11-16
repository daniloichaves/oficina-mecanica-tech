package com.oficina.billingservice.infrastructure.event;

import com.oficina.billingservice.domain.entity.Orcamento;

public interface BillingEventPublisher {
    void publish(String routingKey, Orcamento orcamento);
}
