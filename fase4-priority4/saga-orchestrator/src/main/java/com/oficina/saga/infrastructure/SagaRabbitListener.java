package com.oficina.saga.infrastructure;

import com.oficina.saga.application.SagaOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class SagaRabbitListener {

    private final SagaOrchestratorService sagaOrchestratorService;

    @RabbitListener(queues = RabbitSagaConfig.SAGA_QUEUE)
    public void onEvent(Map<String, Object> message) {
        if (message == null || message.get("ordemId") == null || message.get("evento") == null) {
            return;
        }
        Long ordemId = ((Number) message.get("ordemId")).longValue();
        String evento = String.valueOf(message.get("evento"));
        log.info("Saga recebeu evento RabbitMQ {} para OS {}", evento, ordemId);

        switch (evento) {
            case "os.criada" -> sagaOrchestratorService.iniciarNovaOrdem(ordemId);
            case "orcamento.aprovado" -> sagaOrchestratorService.avancarEtapa(ordemId, "ORCAMENTO_APROVADO");
            case "orcamento.pago" -> sagaOrchestratorService.avancarEtapa(ordemId, "ORCAMENTO_PAGO");
            case "orcamento.rejeitado" -> sagaOrchestratorService.avancarEtapa(ordemId, "ORCAMENTO_REJEITADO");
            case "execucao.finalizada" -> sagaOrchestratorService.avancarEtapa(ordemId, "EXECUCAO_FINALIZADA");
            case "execucao.entregue" -> sagaOrchestratorService.avancarEtapa(ordemId, "EXECUCAO_ENTREGUE");
            default -> log.debug("Evento {} ignorado pela Saga", evento);
        }
    }
}
