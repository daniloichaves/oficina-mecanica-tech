package com.oficina.billingservice.infrastructure.event;

import com.oficina.billingservice.domain.entity.StatusOrcamento;

import java.math.BigDecimal;
import java.time.Instant;

public record BillingEventMessage(
        Long orcamentoId,
        Long ordemId,
        StatusOrcamento status,
        BigDecimal valor,
        String evento,
        String correlationId,
        Instant timestamp
) {
}
