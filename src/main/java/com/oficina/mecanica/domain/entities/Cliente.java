package com.oficina.mecanica.domain.entities;

import com.oficina.mecanica.domain.valueobjects.CpfCnpj;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cpfCnpj;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String telefone;
    
    @Column
    private String email;
    
    @Column
    private String endereco;
    
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Veiculo> veiculos = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public void setCpfCnpj(String cpfCnpj) {
        new CpfCnpj(cpfCnpj); // Valida
        this.cpfCnpj = cpfCnpj.replaceAll("[^0-9]", "");
    }
    
    public String getCpfCnpjFormatado() {
        CpfCnpj cpfCnpjVo = new CpfCnpj(this.cpfCnpj);
        return cpfCnpjVo.formatado();
    }
}
