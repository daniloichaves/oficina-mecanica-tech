package com.oficina.billingservice.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarOrcamentoRequest(
        @NotNull(message = "Ordem de serviço é obrigatória") Long ordemId,
        @NotBlank(message = "Cliente é obrigatório") String clienteId,
        @NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") BigDecimal valor
) {
}
