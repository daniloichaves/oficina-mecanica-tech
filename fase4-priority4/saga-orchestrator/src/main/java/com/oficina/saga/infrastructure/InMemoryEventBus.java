package com.oficina.saga.infrastructure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryEventBus implements SagaEventBus {

    private final List<String> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(String topic, String payload) {
        events.add(topic + ":" + payload);
    }

    public List<String> getEvents() {
        return List.copyOf(events);
    }
}
