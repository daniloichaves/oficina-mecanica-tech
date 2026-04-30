package com.oficina.mecanica.domain.entities;

import com.oficina.mecanica.domain.valueobjects.Placa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veiculos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String placa;
    
    @Column(nullable = false)
    private String marca;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(nullable = false)
    private Integer ano;
    
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrdemServico> ordensServico = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public void setPlaca(String placa) {
        new Placa(placa); // Valida
        this.placa = placa.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
    
    public String getPlacaFormatada() {
        Placa placaVo = new Placa(this.placa);
        return placaVo.formatada();
    }
}
