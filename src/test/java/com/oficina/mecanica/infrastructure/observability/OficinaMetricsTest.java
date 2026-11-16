package com.oficina.mecanica.infrastructure.observability;

import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OficinaMetricsTest {

    @Test
    void deveRegistrarMetricasDeNegocioEIntegracao() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        OficinaMetrics metrics = new OficinaMetrics(registry);

        metrics.registrarOrdemCriada();
        metrics.registrarTransicaoStatus(StatusOrdemServico.EM_EXECUCAO);
        metrics.registrarTransicaoStatus(StatusOrdemServico.FINALIZADA);
        metrics.registrarErroProcessamentoOs("criar");
        metrics.registrarErroNotificacao();

        assertEquals(1.0, registry.get("oficina.os.criadas").counter().count());
        assertEquals(1.0, registry.get("oficina.os.transicoes").tag("status", "EM_EXECUCAO").counter().count());
        assertEquals(1.0, registry.get("oficina.os.transicoes").tag("status", "FINALIZADA").counter().count());
        assertEquals(1.0, registry.get("oficina.os.erros").tag("operacao", "criar").counter().count());
        assertEquals(1.0, registry.get("oficina.integracao.notificacao.erros").counter().count());
    }
}
