package com.oficina.mecanica.infrastructure.observability;

import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OficinaMetrics {

    private final MeterRegistry meterRegistry;

    public OficinaMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void registrarOrdemCriada() {
        Counter.builder("oficina.os.criadas")
            .description("Total de ordens de serviço criadas")
            .register(meterRegistry)
            .increment();
    }

    public void registrarTransicaoStatus(StatusOrdemServico status) {
        Counter.builder("oficina.os.transicoes")
            .description("Total de transições de status das ordens de serviço")
            .tag("status", status != null ? status.name() : "DESCONHECIDO")
            .register(meterRegistry)
            .increment();
    }

    public void registrarErroProcessamentoOs(String operacao) {
        Counter.builder("oficina.os.erros")
            .description("Total de falhas no processamento de ordens de serviço")
            .tag("operacao", operacao != null ? operacao : "desconhecida")
            .register(meterRegistry)
            .increment();
    }

    public void registrarErroNotificacao() {
        Counter.builder("oficina.integracao.notificacao.erros")
            .description("Total de falhas no envio de notificações externas")
            .register(meterRegistry)
            .increment();
    }
}
