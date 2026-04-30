package com.oficina.mecanica.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PecaDTO {
    private Long id;
    
    @NotBlank(message = "Nome da peça é obrigatório")
    private String nome;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "Quantidade não pode ser negativa")
    private Integer quantidadeEstoque;
}
