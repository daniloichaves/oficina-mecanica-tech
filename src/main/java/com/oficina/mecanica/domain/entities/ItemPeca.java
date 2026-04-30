package com.oficina.mecanica.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itens_peca")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPeca {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;
    
    @Column(nullable = false)
    private Integer quantidade;
    
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal valorUnitario;
    
    @PrePersist
    @PreUpdate
    protected void onPersistOrUpdate() {
        if (peca != null) {
            this.valorUnitario = peca.getValor();
        }
    }
}
