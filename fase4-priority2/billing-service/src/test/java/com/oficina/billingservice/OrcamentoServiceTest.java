package com.oficina.billingservice;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.dto.MercadoPagoWebhookRequest;
import com.oficina.billingservice.application.service.OrcamentoService;
import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.infrastructure.event.BillingEventPublisher;
import com.oficina.billingservice.infrastructure.payment.MercadoPagoPaymentGateway;
import com.oficina.billingservice.infrastructure.persistence.OrcamentoRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({OrcamentoService.class, MercadoPagoPaymentGateway.class, SimpleMeterRegistry.class, OrcamentoServiceTest.TestMessagingConfig.class})
class OrcamentoServiceTest {

    @TestConfiguration
    static class TestMessagingConfig {
        @Bean
        BillingEventPublisher billingEventPublisher() {
            return (routingKey, orcamento) -> { };
        }
    }

    @Autowired private OrcamentoService orcamentoService;
    @Autowired private OrcamentoRepository orcamentoRepository;

    @Test
    void deveCriarAprovarPagarECompensarOrcamento() {
        Orcamento criado = orcamentoService.criar(new CriarOrcamentoRequest(99L, "CLIENTE-123", new BigDecimal("350.00")));
        assertThat(criado.getStatus()).isEqualTo(StatusOrcamento.PENDENTE);
        assertThat(orcamentoService.listarTodos()).hasSize(1);
        assertThat(orcamentoService.buscarPorOrdemId(99L)).hasSize(1);

        Orcamento aprovado = orcamentoService.aprovar(criado.getId());
        assertThat(aprovado.getStatus()).isEqualTo(StatusOrcamento.APROVADO);

        Orcamento pago = orcamentoService.pagar(criado.getId());
        assertThat(pago.getStatus()).isEqualTo(StatusOrcamento.PAGO);
        assertThat(pago.getPaymentId()).startsWith("mp-sandbox-");
        assertThat(pago.getPaymentProvider()).isEqualTo("MERCADO_PAGO_SANDBOX");

        Orcamento estornado = orcamentoService.compensar(criado.getId());
        assertThat(estornado.getStatus()).isEqualTo(StatusOrcamento.ESTORNADO);
        assertThat(estornado.getPaymentStatus()).isEqualTo("refunded");
    }

    @Test
    void deveRejeitarEProcessarWebhooksMercadoPagoEValidarTransicoes() {
        Orcamento pendente = orcamentoService.criar(new CriarOrcamentoRequest(77L, "CLI-77", new BigDecimal("150.50")));
        Orcamento rejeitado = orcamentoService.rejeitar(pendente.getId());
        assertThat(rejeitado.getStatus()).isEqualTo(StatusOrcamento.REJEITADO);

        assertThatThrownBy(() -> orcamentoService.aprovar(rejeitado.getId())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orcamentoService.rejeitar(rejeitado.getId())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orcamentoService.pagar(rejeitado.getId())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orcamentoService.buscarPorId(8888L)).isInstanceOf(IllegalArgumentException.class);

        Orcamento viaWebhookAprovado = orcamentoService.criar(new CriarOrcamentoRequest(78L, "CLI-78", new BigDecimal("200.00")));
        Orcamento pagoWebhook = orcamentoService.processarWebhookMercadoPago(
                new MercadoPagoWebhookRequest(viaWebhookAprovado.getId(), "mp-999", "approved"));
        assertThat(pagoWebhook.getStatus()).isEqualTo(StatusOrcamento.PAGO);

        Orcamento viaWebhookRejeitado = orcamentoService.criar(new CriarOrcamentoRequest(79L, "CLI-79", new BigDecimal("120.00")));
        Orcamento rejWebhook = orcamentoService.processarWebhookMercadoPago(
                new MercadoPagoWebhookRequest(viaWebhookRejeitado.getId(), "mp-1000", "rejected"));
        assertThat(rejWebhook.getStatus()).isEqualTo(StatusOrcamento.REJEITADO);

        Orcamento compensaPendente = orcamentoService.criar(new CriarOrcamentoRequest(80L, "CLI-80", new BigDecimal("50.00")));
        assertThat(orcamentoService.compensar(compensaPendente.getId()).getStatus()).isEqualTo(StatusOrcamento.REJEITADO);
        assertThat(orcamentoRepository.count()).isEqualTo(4);
    }
}
