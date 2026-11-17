package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.domain.entity.StatusOrdemServico;

import java.time.Instant;

public record OsEventMessage(
        Long ordemId,
        StatusOrdemServico status,
        String evento,
        String correlationId,
        Instant timestamp
) {
}
