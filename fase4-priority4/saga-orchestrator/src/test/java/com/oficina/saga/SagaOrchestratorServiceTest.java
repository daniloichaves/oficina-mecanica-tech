package com.oficina.saga;

import com.oficina.saga.application.SagaOrchestratorService;
import com.oficina.saga.application.SagaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SagaOrchestratorServiceTest {

    @Autowired
    private SagaOrchestratorService service;

    @Test
    void deveIniciarSagaQuandoOrdemForCriada() {
        var id = 42L;

        var saga = service.iniciarNovaOrdem(id);

        assertThat(saga.getOrdemId()).isEqualTo(id);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.ORCAMENTO_EM_ANDAMENTO);
        assertThat(saga.getMensagem()).contains("orcamento");
    }

    @Test
    void deveAvancarParaExecucaoQuandoOrcamentoForAprovado() {
        var id = 43L;
        service.iniciarNovaOrdem(id);

        var saga = service.avancarEtapa(id, "ORCAMENTO_APROVADO");

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.EXECUCAO_EM_ANDAMENTO);
        assertThat(saga.getMensagem()).contains("execucao");
    }
}
