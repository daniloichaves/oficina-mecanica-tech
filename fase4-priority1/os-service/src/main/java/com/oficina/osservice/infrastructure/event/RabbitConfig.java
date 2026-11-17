package com.oficina.osservice.infrastructure.event;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class RabbitConfig {

    public static final String COMMAND_QUEUE = "os-service.status-commands";

    @Bean
    public TopicExchange oficinaEventsExchange(@Value("${app.messaging.exchange:oficina.events}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue osStatusCommandQueue() {
        return new Queue(COMMAND_QUEUE, true);
    }

    @Bean
    public Binding osStatusCommandBinding(Queue osStatusCommandQueue, TopicExchange oficinaEventsExchange) {
        return BindingBuilder.bind(osStatusCommandQueue).to(oficinaEventsExchange).with("saga.os.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
