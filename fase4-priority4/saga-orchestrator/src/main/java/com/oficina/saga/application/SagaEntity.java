package com.oficina.saga.application;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga")
@Getter
@Setter
@NoArgsConstructor
public class SagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long ordemId;

    private String clienteId;

    private Long orcamentoId;

    private String execucaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(length = 2000)
    private String historicoEtapas;

    private String correlationId;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dataCriacao = now;
        this.dataAtualizacao = now;
    }

    @PreUpdate
    void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void registrarEtapa(String etapa) {
        if (this.historicoEtapas == null || this.historicoEtapas.isBlank()) {
            this.historicoEtapas = etapa;
        } else {
            this.historicoEtapas = this.historicoEtapas + " -> " + etapa;
        }
    }
}
