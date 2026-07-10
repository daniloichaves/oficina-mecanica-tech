package com.oficina.mecanica.application.dto;

import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusOrdemServicoDTO {
    private Long id;
    private StatusOrdemServico status;
    private String descricao;
}
