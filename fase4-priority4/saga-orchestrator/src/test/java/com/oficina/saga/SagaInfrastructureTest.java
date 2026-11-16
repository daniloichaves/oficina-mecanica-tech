package com.oficina.saga;

import com.oficina.saga.application.SagaOrchestratorService;
import com.oficina.saga.infrastructure.*;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SagaInfrastructureTest {

    @Test
    void deveChamarMicrosservicosViaRestEPropagarCorrelationId() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/api/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String json = path.contains("execucoes") ? "{\"id\":\"exec-mongo-1\"}" : "{\"id\":123}";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        try {
            String base = "http://localhost:" + server.getAddress().getPort();
            RestMicroserviceGateway gw = new RestMicroserviceGateway(base, base, base);
            assertThat(gw.criarOrcamento(1L, "cli", new BigDecimal("100.00"))).isEqualTo(123L);
            assertThat(gw.criarExecucao(1L, "cli", "freio")).isEqualTo("exec-mongo-1");
            gw.atualizarStatusOs(1L, "EM_EXECUCAO");
            gw.compensarOrcamento(123L);
            gw.compensarOrcamento(null);
            gw.cancelarOs(1L);
        } finally {
            server.stop(0);
        }

        CorrelationIdFilter filter = new CorrelationIdFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = (r, s) -> { };
        filter.doFilter(req, res, chain);
        assertThat(res.getHeader(CorrelationIdFilter.HEADER_NAME)).isNotBlank();
    }

    @Test
    void deveConfigurarRabbitEProcessarEventosNoListener() {
        RabbitSagaConfig cfg = new RabbitSagaConfig();
        var exchange = cfg.oficinaEventsExchange("oficina.events");
        var queue = cfg.sagaEventsQueue();
        assertThat(cfg.sagaOsBinding(queue, exchange)).isNotNull();
        assertThat(cfg.sagaBillingBinding(queue, exchange)).isNotNull();
        assertThat(cfg.sagaExecutionBinding(queue, exchange)).isNotNull();
        assertThat(cfg.jackson2JsonMessageConverter()).isNotNull();

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitSagaEventBus bus = new RabbitSagaEventBus(rabbitTemplate, "oficina.events");
        bus.publish("os.criada", "payload");
        verify(rabbitTemplate).convertAndSend(eq("oficina.events"), eq("os.criada"), any(Map.class));

        SagaOrchestratorService sagaService = mock(SagaOrchestratorService.class);
        SagaRabbitListener listener = new SagaRabbitListener(sagaService);
        listener.onEvent(null);
        listener.onEvent(Map.of("ordemId", 1, "evento", "os.criada"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "orcamento.aprovado"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "orcamento.pago"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "orcamento.rejeitado"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "execucao.finalizada"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "execucao.entregue"));
        listener.onEvent(Map.of("ordemId", 1, "evento", "outro"));

        verify(sagaService).iniciarNovaOrdem(1L);
        verify(sagaService).avancarEtapa(1L, "ORCAMENTO_APROVADO");
        verify(sagaService).avancarEtapa(1L, "ORCAMENTO_PAGO");
        verify(sagaService).avancarEtapa(1L, "ORCAMENTO_REJEITADO");
        verify(sagaService).avancarEtapa(1L, "EXECUCAO_FINALIZADA");
        verify(sagaService).avancarEtapa(1L, "EXECUCAO_ENTREGUE");
    }
}
