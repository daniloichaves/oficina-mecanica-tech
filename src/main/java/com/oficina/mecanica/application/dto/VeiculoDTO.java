package com.oficina.mecanica.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDTO {
    private Long id;
    
    @NotBlank(message = "Placa é obrigatória")
    private String placa;
    
    @NotBlank(message = "Marca é obrigatória")
    private String marca;
    
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;
    
    @NotNull(message = "Ano é obrigatório")
    @Positive(message = "Ano deve ser positivo")
    private Integer ano;
    
    private Long clienteId;
}
