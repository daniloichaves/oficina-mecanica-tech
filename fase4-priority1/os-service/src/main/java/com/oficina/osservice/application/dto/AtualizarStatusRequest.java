package com.oficina.osservice.application.dto;

import com.oficina.osservice.domain.entity.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull StatusOrdemServico status) {
}
