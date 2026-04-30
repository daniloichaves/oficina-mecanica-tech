package com.oficina.mecanica.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarOrdemServicoDTO {
    
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;
    
    @NotNull(message = "ID do veículo é obrigatório")
    private Long veiculoId;
    
    @NotEmpty(message = "Pelo menos um serviço deve ser informado")
    private List<ItemServicoDTO> itensServico;
    
    private List<ItemPecaDTO> itensPeca;
}
