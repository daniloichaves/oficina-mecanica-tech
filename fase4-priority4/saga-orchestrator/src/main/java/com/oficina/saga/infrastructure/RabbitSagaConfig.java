package com.oficina.saga.infrastructure;

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
public class RabbitSagaConfig {

    public static final String SAGA_QUEUE = "saga-orchestrator.events";

    @Bean
    public TopicExchange oficinaEventsExchange(@Value("${app.messaging.exchange:oficina.events}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue sagaEventsQueue() {
        return new Queue(SAGA_QUEUE, true);
    }

    @Bean
    public Binding sagaOsBinding(Queue sagaEventsQueue, TopicExchange oficinaEventsExchange) {
        return BindingBuilder.bind(sagaEventsQueue).to(oficinaEventsExchange).with("os.criada");
    }

    @Bean
    public Binding sagaBillingBinding(Queue sagaEventsQueue, TopicExchange oficinaEventsExchange) {
        return BindingBuilder.bind(sagaEventsQueue).to(oficinaEventsExchange).with("orcamento.*");
    }

    @Bean
    public Binding sagaExecutionBinding(Queue sagaEventsQueue, TopicExchange oficinaEventsExchange) {
        return BindingBuilder.bind(sagaEventsQueue).to(oficinaEventsExchange).with("execucao.*");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
