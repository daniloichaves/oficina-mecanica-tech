package com.oficina.osservice.infrastructure.event;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class OsCommandListener {

    private final OrdemServicoService ordemServicoService;

    @RabbitListener(queues = RabbitConfig.COMMAND_QUEUE)
    public void onCommand(OsEventMessage command) {
        if (command == null || command.ordemId() == null || command.status() == null) {
            return;
        }
        log.info("Recebido comando assíncrono {} para OS {} -> {}", command.evento(), command.ordemId(), command.status());
        if (command.status() == StatusOrdemServico.CANCELADA) {
            ordemServicoService.cancelar(command.ordemId());
        } else {
            ordemServicoService.atualizarStatus(command.ordemId(), new AtualizarStatusRequest(command.status()));
        }
    }
}
