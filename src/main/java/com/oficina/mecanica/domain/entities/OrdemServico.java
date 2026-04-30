package com.oficina.mecanica.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdemServico status;
    
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(name = "orcamento_aprovado")
    private Boolean orcamentoAprovado;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;
    
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ItemServico> itensServico = new ArrayList<>();
    
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ItemPeca> itensPeca = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (status == null) {
            status = StatusOrdemServico.RECEBIDA;
        }
        if (orcamentoAprovado == null) {
            orcamentoAprovado = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public void calcularOrcamento() {
        BigDecimal totalServicos = itensServico.stream()
            .map(item -> item.getServico().getValor().multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPecas = itensPeca.stream()
            .map(item -> item.getPeca().getValor().multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.valorTotal = totalServicos.add(totalPecas);
    }
    
    public void aprovarOrcamento() {
        if (status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Só é possível aprovar orçamento quando status é AGUARDANDO_APROVACAO");
        }
        this.orcamentoAprovado = true;
        this.status = StatusOrdemServico.EM_EXECUCAO;
    }
    
    public void iniciarDiagnostico() {
        if (status != StatusOrdemServico.RECEBIDA) {
            throw new IllegalStateException("Só é possível iniciar diagnóstico quando status é RECEBIDA");
        }
        this.status = StatusOrdemServico.EM_DIAGNOSTICO;
    }
    
    public void concluirDiagnostico() {
        if (status != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Só é possível concluir diagnóstico quando status é EM_DIAGNOSTICO");
        }
        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
        calcularOrcamento();
    }
    
    public void finalizar() {
        if (status != StatusOrdemServico.EM_EXECUCAO) {
            throw new IllegalStateException("Só é possível finalizar quando status é EM_EXECUCAO");
        }
        this.status = StatusOrdemServico.FINALIZADA;
    }
    
    public void entregar() {
        if (status != StatusOrdemServico.FINALIZADA) {
            throw new IllegalStateException("Só é possível entregar quando status é FINALIZADA");
        }
        this.status = StatusOrdemServico.ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }
}
