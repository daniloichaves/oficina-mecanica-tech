package com.oficina.executionservice.infrastructure.event;

import com.oficina.executionservice.domain.entity.Execucao;

public interface ExecutionEventPublisher {
    void publish(String routingKey, Execucao execucao);
}
