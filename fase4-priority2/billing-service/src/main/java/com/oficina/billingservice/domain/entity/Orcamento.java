package com.oficina.billingservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordemId;

    @Column(nullable = false)
    private String clienteId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrcamento status;

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
            this.status = StatusOrcamento.PENDENTE;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void aprovar() {
        if (this.status != StatusOrcamento.PENDENTE) {
            throw new IllegalStateException("Só é possível aprovar um orçamento pendente");
        }
        this.status = StatusOrcamento.APROVADO;
    }

    public void rejeitar() {
        if (this.status != StatusOrcamento.PENDENTE) {
            throw new IllegalStateException("Só é possível rejeitar um orçamento pendente");
        }
        this.status = StatusOrcamento.REJEITADO;
    }

    public void pagar() {
        if (this.status != StatusOrcamento.APROVADO) {
            throw new IllegalStateException("Só é possível pagar um orçamento aprovado");
        }
        this.status = StatusOrcamento.PAGO;
    }
}
