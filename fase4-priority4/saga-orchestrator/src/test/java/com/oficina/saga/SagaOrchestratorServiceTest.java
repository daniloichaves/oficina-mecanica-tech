package com.oficina.saga;

import com.oficina.saga.application.SagaEntity;
import com.oficina.saga.application.SagaOrchestratorService;
import com.oficina.saga.application.SagaRepository;
import com.oficina.saga.application.SagaStatus;
import com.oficina.saga.infrastructure.InMemoryEventBus;
import com.oficina.saga.infrastructure.MicroserviceGateway;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SagaOrchestratorServiceTest {

    @Autowired private SagaOrchestratorService service;
    @Autowired private SagaRepository repository;
    @Autowired private InMemoryEventBus eventBus;

    @Test
    void deveExecutarFluxoCompletoEListarSagas() {
        var id = 42L;
        var saga = service.iniciarNovaOrdem(id);
        assertThat(saga.getOrdemId()).isEqualTo(id);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.ORCAMENTO_EM_ANDAMENTO);
        assertThat(service.listarTodas()).isNotEmpty();

        var exec = service.avancarEtapa(id, "ORCAMENTO_APROVADO");
        assertThat(exec.getStatus()).isEqualTo(SagaStatus.EXECUCAO_EM_ANDAMENTO);

        var desconhecido = service.avancarEtapa(id, "PING");
        assertThat(desconhecido.getHistoricoEtapas()).contains("EVENTO_PING");

        var fim = service.avancarEtapa(id, "EXECUCAO_ENTREGUE");
        assertThat(fim.getStatus()).isEqualTo(SagaStatus.FINALIZADA);
        assertThat(eventBus.getEvents()).isNotEmpty();
    }

    @Test
    void deveCompensarQuandoFalharCriacaoOrcamentoOuExecucao() {
        MicroserviceGateway failingGateway = mock(MicroserviceGateway.class);
        when(failingGateway.criarOrcamento(anyLong(), anyString(), any())).thenThrow(new RuntimeException("Billing indisponível"));
        SagaOrchestratorService isolated = new SagaOrchestratorService(repository, eventBus, failingGateway);
        ReflectionTestUtils.setField(isolated, "meterRegistry", new SimpleMeterRegistry());

        SagaEntity compOrcamento = isolated.iniciarNovaOrdem(901L, "cli-901", new BigDecimal("100.00"), "Teste");
        assertThat(compOrcamento.getStatus()).isEqualTo(SagaStatus.COMPENSADA);
        verify(failingGateway).cancelarOs(901L);

        MicroserviceGateway failExecGateway = mock(MicroserviceGateway.class);
        when(failExecGateway.criarOrcamento(eq(902L), anyString(), any())).thenReturn(777L);
        when(failExecGateway.criarExecucao(eq(902L), anyString(), any())).thenThrow(new RuntimeException("Mongo fora"));
        SagaOrchestratorService isolatedExec = new SagaOrchestratorService(repository, eventBus, failExecGateway);

        isolatedExec.iniciarNovaOrdem(902L, "cli-902", new BigDecimal("300.00"), "Motor");
        SagaEntity compExec = isolatedExec.avancarEtapa(902L, "ORCAMENTO_PAGO");
        assertThat(compExec.getStatus()).isEqualTo(SagaStatus.COMPENSADA);
        verify(failExecGateway).compensarOrcamento(777L);
        verify(failExecGateway).cancelarOs(902L);

        assertThatThrownBy(() -> service.buscarPorOrdemId(999999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
