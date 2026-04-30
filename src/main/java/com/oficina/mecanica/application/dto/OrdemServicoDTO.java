package com.oficina.mecanica.application.dto;

import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long veiculoId;
    private String veiculoPlaca;
    private StatusOrdemServico status;
    private BigDecimal valorTotal;
    private Boolean orcamentoAprovado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataEntrega;
    private List<ItemServicoDTO> itensServico;
    private List<ItemPecaDTO> itensPeca;
}
