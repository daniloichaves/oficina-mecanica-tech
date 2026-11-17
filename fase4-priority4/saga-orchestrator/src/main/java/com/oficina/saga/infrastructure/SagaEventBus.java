package com.oficina.saga.infrastructure;

public interface SagaEventBus {
    void publish(String topic, String payload);
}
