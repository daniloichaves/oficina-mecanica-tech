package com.oficina.mecanica.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoOrcamentoDTO {

    @NotNull(message = "ordemServicoId é obrigatório")
    private Long ordemServicoId;

    @NotNull(message = "aprovado é obrigatório")
    private Boolean aprovado;
}
