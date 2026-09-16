package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.domain.entity.StatusOrdemServico;

public interface OsEventPublisher {
    void publishStatusChanged(String ordemId, StatusOrdemServico status);
}
