package com.oficina.billingservice;

import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.infrastructure.event.BillingEventMessage;
import com.oficina.billingservice.infrastructure.event.NoOpBillingEventPublisher;
import com.oficina.billingservice.infrastructure.event.RabbitBillingConfig;
import com.oficina.billingservice.infrastructure.event.RabbitBillingEventPublisher;
import com.oficina.billingservice.infrastructure.observability.CorrelationIdFilter;
import com.oficina.billingservice.infrastructure.payment.MercadoPagoPaymentGateway;
import com.oficina.billingservice.infrastructure.payment.PaymentGateway;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BillingMessagingAndPaymentTest {

    @Test
    void deveExecutarSandboxEIntegracaoHttpMercadoPago() throws Exception {
        MercadoPagoPaymentGateway sandbox = new MercadoPagoPaymentGateway("https://api.mercadopago.com", "");
        Orcamento orc = Orcamento.builder().id(5L).ordemId(10L).clienteId("cli")
                .valor(new BigDecimal("100.00")).status(StatusOrcamento.APROVADO).build();

        PaymentGateway.PaymentResult res = sandbox.processarPagamento(orc);
        assertThat(res.status()).isEqualTo("approved");
        assertThat(res.provider()).isEqualTo("MERCADO_PAGO_SANDBOX");
        sandbox.estornarPagamento(res.paymentId());
        sandbox.estornarPagamento(null);

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/payments", exchange -> {
            byte[] body = exchange.getRequestURI().getPath().endsWith("/refunds")
                    ? "{}".getBytes(StandardCharsets.UTF_8)
                    : "{\"id\":98765,\"status\":\"approved\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        try {
            String baseUrl = "http://localhost:" + server.getAddress().getPort();
            MercadoPagoPaymentGateway live = new MercadoPagoPaymentGateway(baseUrl, "token-teste");
            PaymentGateway.PaymentResult liveRes = live.processarPagamento(orc);
            assertThat(liveRes.paymentId()).isEqualTo("98765");
            assertThat(liveRes.provider()).isEqualTo("MERCADO_PAGO");
            live.estornarPagamento("98765");
        } finally {
            server.stop(0);
        }
    }

    @Test
    void devePublicarEventosRabbitENoOpEPropagarCorrelationId() throws Exception {
        RabbitBillingConfig cfg = new RabbitBillingConfig();
        assertThat(cfg.oficinaEventsExchange("oficina.events").getName()).isEqualTo("oficina.events");
        assertThat(cfg.jackson2JsonMessageConverter()).isNotNull();

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitBillingEventPublisher publisher = new RabbitBillingEventPublisher(rabbitTemplate, "oficina.events");
        Orcamento orc = Orcamento.builder().id(2L).ordemId(20L).clienteId("cli")
                .valor(new BigDecimal("80.00")).status(StatusOrcamento.PAGO).build();
        publisher.publish("orcamento.pago", orc);
        verify(rabbitTemplate).convertAndSend(eq("oficina.events"), eq("orcamento.pago"), any(BillingEventMessage.class));

        new NoOpBillingEventPublisher().publish("orcamento.criado", orc);

        CorrelationIdFilter filter = new CorrelationIdFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(CorrelationIdFilter.HEADER_NAME, "corr-billing");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (r, s) -> { };
        filter.doFilter(req, resp, chain);
        assertThat(resp.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("corr-billing");
    }
}
