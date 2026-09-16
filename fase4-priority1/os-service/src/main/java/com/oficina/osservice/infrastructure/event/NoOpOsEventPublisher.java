package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.domain.entity.StatusOrdemServico;
import org.springframework.stereotype.Component;

@Component
public class NoOpOsEventPublisher implements OsEventPublisher {

    @Override
    public void publishStatusChanged(String ordemId, StatusOrdemServico status) {
        // placeholder para o próximo passo: integrar com broker/event bus.
    }
}
