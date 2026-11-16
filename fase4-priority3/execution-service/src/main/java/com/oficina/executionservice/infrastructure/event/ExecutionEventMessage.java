package com.oficina.executionservice.infrastructure.event;

import com.oficina.executionservice.domain.entity.StatusExecucao;

import java.time.Instant;

public record ExecutionEventMessage(
        String execucaoId,
        Long ordemId,
        StatusExecucao status,
        String evento,
        String correlationId,
        Instant timestamp
) {
}
