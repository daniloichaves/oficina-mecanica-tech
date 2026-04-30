package com.oficina.mecanica.domain.entities;

public enum StatusOrdemServico {
    RECEBIDA("Recebida"),
    EM_DIAGNOSTICO("Em Diagnóstico"),
    AGUARDANDO_APROVACAO("Aguardando Aprovação"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADA("Finalizada"),
    ENTREGUE("Entregue");
    
    private final String descricao;
    
    StatusOrdemServico(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
