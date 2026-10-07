package com.oficina.executionservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "execucoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Execucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordemId;

    @Column(nullable = false)
    private String clienteId;

    @Column(nullable = false)
    private String descricaoTarefa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExecucao status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dataCriacao = now;
        this.dataAtualizacao = now;
        if (this.status == null) {
            this.status = StatusExecucao.AGUARDANDO;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void iniciar() {
        if (this.status != StatusExecucao.AGUARDANDO && this.status != StatusExecucao.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Só é possível iniciar uma execução pendente ou em diagnóstico");
        }
        this.status = StatusExecucao.EM_EXECUCAO;
    }

    public void finalizar() {
        if (this.status != StatusExecucao.EM_EXECUCAO) {
            throw new IllegalStateException("Só é possível finalizar quando a execução está em andamento");
        }
        this.status = StatusExecucao.FINALIZADA;
    }

    public void entregar() {
        if (this.status != StatusExecucao.FINALIZADA) {
            throw new IllegalStateException("Só é possível entregar quando a execução está finalizada");
        }
        this.status = StatusExecucao.ENTREGUE;
    }
}
