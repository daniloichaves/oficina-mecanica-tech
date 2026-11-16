package com.oficina.saga.bdd;

import com.oficina.saga.application.SagaEntity;
import com.oficina.saga.application.SagaOrchestratorService;
import com.oficina.saga.application.SagaStatus;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest
public class SagaStepDefinitions {

    @Autowired
    private SagaOrchestratorService sagaOrchestratorService;

    @Dado("que uma nova ordem de serviço {long} foi aberta para o cliente {string}")
    public void abrirNovaOrdem(Long ordemId, String clienteId) {
        SagaEntity saga = sagaOrchestratorService.iniciarNovaOrdem(
                ordemId, clienteId, new BigDecimal("750.00"), "Revisão completa");
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.ORCAMENTO_EM_ANDAMENTO);
    }

    @Quando("o orçamento da ordem {long} for aprovado")
    public void aprovarOrcamento(Long ordemId) {
        SagaEntity saga = sagaOrchestratorService.avancarEtapa(ordemId, "ORCAMENTO_APROVADO");
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.EXECUCAO_EM_ANDAMENTO);
    }

    @Quando("o orçamento da ordem {long} for rejeitado")
    public void rejeitarOrcamento(Long ordemId) {
        sagaOrchestratorService.avancarEtapa(ordemId, "ORCAMENTO_REJEITADO");
    }

    @Quando("ocorrer uma falha na etapa de execução da ordem {long}")
    public void falhaNaExecucao(Long ordemId) {
        sagaOrchestratorService.avancarEtapa(ordemId, "EXECUCAO_FALHOU");
    }

    @E("a execução da ordem {long} for finalizada")
    public void finalizarExecucao(Long ordemId) {
        sagaOrchestratorService.avancarEtapa(ordemId, "EXECUCAO_FINALIZADA");
    }

    @Então("o status final da saga da ordem {long} deve ser {string}")
    public void verificarStatusFinal(Long ordemId, String statusEsperado) {
        SagaEntity saga = sagaOrchestratorService.buscarPorOrdemId(ordemId);
        assertThat(saga.getStatus()).isEqualTo(SagaStatus.valueOf(statusEsperado));
    }

    @E("o histórico da saga da ordem {long} deve conter {string}")
    public void verificarHistoricoUmItem(Long ordemId, String etapa) {
        SagaEntity saga = sagaOrchestratorService.buscarPorOrdemId(ordemId);
        assertThat(saga.getHistoricoEtapas()).contains(etapa);
    }

    @E("o histórico da saga da ordem {long} deve conter {string} e {string}")
    public void verificarHistoricoDoisItens(Long ordemId, String e1, String e2) {
        SagaEntity saga = sagaOrchestratorService.buscarPorOrdemId(ordemId);
        assertThat(saga.getHistoricoEtapas()).contains(e1, e2);
    }

    @E("o histórico da saga da ordem {long} deve conter {string} e {string} e {string}")
    public void verificarHistoricoTresItens(Long ordemId, String e1, String e2, String e3) {
        SagaEntity saga = sagaOrchestratorService.buscarPorOrdemId(ordemId);
        assertThat(saga.getHistoricoEtapas()).contains(e1, e2, e3);
    }
}
