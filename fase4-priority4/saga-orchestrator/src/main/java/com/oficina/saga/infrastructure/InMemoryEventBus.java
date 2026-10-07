package com.oficina.saga.infrastructure;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryEventBus {

    private final List<String> events = new CopyOnWriteArrayList<>();

    public void publish(String topic, String payload) {
        events.add(topic + ":" + payload);
    }

    public List<String> getEvents() {
        return List.copyOf(events);
    }
}
