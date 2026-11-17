package com.oficina.saga.infrastructure;

import java.math.BigDecimal;

public interface MicroserviceGateway {
    Long criarOrcamento(Long ordemId, String clienteId, BigDecimal valor);
    void compensarOrcamento(Long orcamentoId);
    String criarExecucao(Long ordemId, String clienteId, String descricao);
    void atualizarStatusOs(Long ordemId, String status);
    void cancelarOs(Long ordemId);
}
