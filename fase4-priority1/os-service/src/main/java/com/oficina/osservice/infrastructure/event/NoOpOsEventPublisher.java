package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.domain.entity.StatusOrdemServico;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpOsEventPublisher implements OsEventPublisher {

    @Override
    public void publishStatusChanged(String ordemId, StatusOrdemServico status) {
        log.debug("Mensageria desativada - evento local OS {} -> {}", ordemId, status);
    }
}
