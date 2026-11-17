package com.oficina.billingservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MercadoPagoWebhookRequest(
        @NotNull Long orcamentoId,
        @NotBlank String paymentId,
        @NotBlank String status
) {
}
