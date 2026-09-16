package com.oficina.saga.application;

import com.oficina.saga.infrastructure.InMemoryEventBus;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestratorService {

    private final SagaRepository repository;
    private final InMemoryEventBus eventBus;

    public SagaOrchestratorService(SagaRepository repository, InMemoryEventBus eventBus) {
        this.repository = repository;
        this.eventBus = eventBus;
    }

    public SagaEntity iniciarNovaOrdem(Long ordemId) {
        return repository.findByOrdemId(ordemId)
                .orElseGet(() -> {
                    SagaEntity saga = new SagaEntity();
                    saga.setOrdemId(ordemId);
                    saga.setStatus(SagaStatus.ORCAMENTO_EM_ANDAMENTO);
                    saga.setMensagem("Nova ordem recebida. Evento de orcamento foi disparado para a etapa inicial da saga.");
                    eventBus.publish("os.criada", "OS " + ordemId + " iniciada no fluxo de orcamento");
                    return repository.save(saga);
                });
    }

    public SagaEntity avancarEtapa(Long ordemId, String evento) {
        SagaEntity saga = repository.findByOrdemId(ordemId)
                .orElseThrow(() -> new IllegalArgumentException("Saga não encontrada para a ordem " + ordemId));

        if ("ORCAMENTO_APROVADO".equals(evento)) {
            saga.setStatus(SagaStatus.EXECUCAO_EM_ANDAMENTO);
            saga.setMensagem("Orcamento aprovado. Proxima etapa: execucao da ordem de servico.");
            eventBus.publish("orcamento.aprovado", "OS " + ordemId + " aprovada e enviada para execucao");
            return repository.save(saga);
        }

        if ("EXECUCAO_FINALIZADA".equals(evento)) {
            saga.setStatus(SagaStatus.FINALIZADA);
            saga.setMensagem("Execução concluída com sucesso. Ordem finalizada e entregue ao cliente.");
            eventBus.publish("execucao.finalizada", "OS " + ordemId + " concluída com sucesso");
            return repository.save(saga);
        }

        if ("ORCAMENTO_REJEITADO".equals(evento)) {
            saga.setStatus(SagaStatus.CANCELADA);
            saga.setMensagem("Orçamento rejeitado. A saga foi compensada para evitar execução inconsistentes.");
            eventBus.publish("orcamento.rejeitado", "OS " + ordemId + " foi compensada");
            return repository.save(saga);
        }

        saga.setMensagem("Evento recebido: " + evento + ". Ainda aguardando transição de estado.");
        return repository.save(saga);
    }
}
