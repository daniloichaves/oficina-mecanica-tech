package com.oficina.executionservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarExecucaoRequest(
        @NotNull(message = "Ordem de serviço é obrigatória") Long ordemId,
        @NotBlank(message = "Cliente é obrigatório") String clienteId,
        @NotBlank(message = "Descrição da tarefa é obrigatória") String descricaoTarefa
) {
}
