package com.oficina.executionservice;

import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.infrastructure.event.ExecutionEventMessage;
import com.oficina.executionservice.infrastructure.event.NoOpExecutionEventPublisher;
import com.oficina.executionservice.infrastructure.event.RabbitExecutionConfig;
import com.oficina.executionservice.infrastructure.event.RabbitExecutionEventPublisher;
import com.oficina.executionservice.infrastructure.observability.CorrelationIdFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExecutionMessagingAndObservabilityTest {

    @Test
    void devePublicarEventosRabbitENoOpEPropagarCorrelationId() throws Exception {
        RabbitExecutionConfig cfg = new RabbitExecutionConfig();
        assertThat(cfg.oficinaEventsExchange("oficina.events").getName()).isEqualTo("oficina.events");
        assertThat(cfg.jackson2JsonMessageConverter()).isNotNull();

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitExecutionEventPublisher publisher = new RabbitExecutionEventPublisher(rabbitTemplate, "oficina.events");
        Execucao exec = Execucao.builder().id("exec-1").ordemId(5L).status(StatusExecucao.FINALIZADA).build();

        publisher.publish("execucao.finalizada", exec);
        verify(rabbitTemplate).convertAndSend(eq("oficina.events"), eq("execucao.finalizada"), any(ExecutionEventMessage.class));

        new NoOpExecutionEventPublisher().publish("execucao.criada", exec);

        CorrelationIdFilter filter = new CorrelationIdFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(CorrelationIdFilter.HEADER_NAME, "corr-exec");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = (r, s) -> { };
        filter.doFilter(req, res, chain);
        assertThat(res.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("corr-exec");
    }
}
