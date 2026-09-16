package com.oficina.executionservice.application.dto;

import com.oficina.executionservice.domain.entity.StatusExecucao;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull StatusExecucao status) {
}
