package com.oficina.executionservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "execucoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Execucao {

    @Id
    private String id;

    @Indexed
    private Long ordemId;

    private String clienteId;

    private String descricaoTarefa;

    private StatusExecucao status;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    public void iniciar() {
        if (this.status != StatusExecucao.AGUARDANDO && this.status != StatusExecucao.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Só é possível iniciar uma execução pendente ou em diagnóstico");
        }
        this.status = StatusExecucao.EM_EXECUCAO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void finalizar() {
        if (this.status != StatusExecucao.EM_EXECUCAO) {
            throw new IllegalStateException("Só é possível finalizar quando a execução está em andamento");
        }
        this.status = StatusExecucao.FINALIZADA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void entregar() {
        if (this.status != StatusExecucao.FINALIZADA) {
            throw new IllegalStateException("Só é possível entregar quando a execução está finalizada");
        }
        this.status = StatusExecucao.ENTREGUE;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusExecucao.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar uma execução já entregue");
        }
        this.status = StatusExecucao.CANCELADA;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
