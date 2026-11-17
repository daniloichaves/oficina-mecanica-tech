package com.oficina.saga.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.orchestration.remote-enabled", havingValue = "false", matchIfMissing = true)
public class LocalMicroserviceGateway implements MicroserviceGateway {

    @Override
    public Long criarOrcamento(Long ordemId, String clienteId, BigDecimal valor) {
        log.debug("[LOCAL] Criando orçamento para OS {} cliente {}", ordemId, clienteId);
        return ordemId * 100L;
    }

    @Override
    public void compensarOrcamento(Long orcamentoId) {
        log.debug("[LOCAL] Compensando orçamento {}", orcamentoId);
    }

    @Override
    public String criarExecucao(Long ordemId, String clienteId, String descricao) {
        log.debug("[LOCAL] Criando execução para OS {}", ordemId);
        return "local-exec-" + ordemId;
    }

    @Override
    public void atualizarStatusOs(Long ordemId, String status) {
        log.debug("[LOCAL] Atualizando status da OS {} para {}", ordemId, status);
    }

    @Override
    public void cancelarOs(Long ordemId) {
        log.debug("[LOCAL] Cancelando OS {} por compensação", ordemId);
    }
}
