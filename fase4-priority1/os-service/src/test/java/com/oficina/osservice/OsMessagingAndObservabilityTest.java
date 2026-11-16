package com.oficina.osservice;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.infrastructure.event.OsCommandListener;
import com.oficina.osservice.infrastructure.event.OsEventMessage;
import com.oficina.osservice.infrastructure.event.RabbitConfig;
import com.oficina.osservice.infrastructure.event.RabbitOsEventPublisher;
import com.oficina.osservice.infrastructure.observability.CorrelationIdFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OsMessagingAndObservabilityTest {

    @Test
    void devePublicarEventosNoRabbitComRoutingKeyCorreta() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitOsEventPublisher publisher = new RabbitOsEventPublisher(rabbitTemplate, "oficina.events");

        publisher.publishStatusChanged("10", StatusOrdemServico.RECEBIDA);
        ArgumentCaptor<OsEventMessage> captor = ArgumentCaptor.forClass(OsEventMessage.class);
        verify(rabbitTemplate).convertAndSend(eq("oficina.events"), eq("os.criada"), captor.capture());
        assertThat(captor.getValue().ordemId()).isEqualTo(10L);

        publisher.publishStatusChanged("10", StatusOrdemServico.EM_EXECUCAO);
        verify(rabbitTemplate).convertAndSend(eq("oficina.events"), eq("os.status.atualizado"), any(OsEventMessage.class));
    }

    @Test
    void deveProcessarComandosAssincronosDeStatusECancelamento() {
        OrdemServicoService service = mock(OrdemServicoService.class);
        OsCommandListener listener = new OsCommandListener(service);

        listener.onCommand(null);
        listener.onCommand(new OsEventMessage(5L, StatusOrdemServico.CANCELADA, "saga.os.cancelar", "c1", Instant.now()));
        verify(service).cancelar(5L);

        listener.onCommand(new OsEventMessage(5L, StatusOrdemServico.FINALIZADA, "saga.os.atualizar", "c1", Instant.now()));
        verify(service).atualizarStatus(5L, new AtualizarStatusRequest(StatusOrdemServico.FINALIZADA));
    }

    @Test
    void deveCriarConfiguracaoRabbitEPropagarCorrelationId() throws Exception {
        RabbitConfig config = new RabbitConfig();
        assertThat(config.oficinaEventsExchange("oficina.events").getName()).isEqualTo("oficina.events");
        assertThat(config.osStatusCommandQueue().getName()).isEqualTo(RabbitConfig.COMMAND_QUEUE);
        assertThat(config.osStatusCommandBinding(config.osStatusCommandQueue(), config.oficinaEventsExchange("oficina.events"))).isNotNull();
        assertThat(config.jackson2JsonMessageConverter()).isNotNull();

        CorrelationIdFilter filter = new CorrelationIdFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(CorrelationIdFilter.HEADER_NAME, "corr-abc");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = (r, s) -> { };
        filter.doFilter(req, res, chain);
        assertThat(res.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("corr-abc");
    }
}
