package com.oficina.saga.application;

import com.oficina.saga.infrastructure.MicroserviceGateway;
import com.oficina.saga.infrastructure.SagaEventBus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SagaOrchestratorService {

    private final SagaRepository repository;
    private final SagaEventBus eventBus;
    private final MicroserviceGateway microserviceGateway;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    public SagaEntity iniciarNovaOrdem(Long ordemId) {
        return iniciarNovaOrdem(ordemId, "cliente-padrao", new BigDecimal("500.00"), "Diagnóstico e reparo OS #" + ordemId);
    }

    public SagaEntity iniciarNovaOrdem(Long ordemId, String clienteId, BigDecimal valorEstimado, String descricao) {
        return repository.findByOrdemId(ordemId)
                .orElseGet(() -> criarEOrquestrarOrcamento(ordemId, clienteId, valorEstimado));
    }

    private SagaEntity criarEOrquestrarOrcamento(Long ordemId, String clienteId, BigDecimal valorEstimado) {
        SagaEntity saga = new SagaEntity();
        saga.setOrdemId(ordemId);
        saga.setClienteId(clienteId);
        saga.setCorrelationId(MDC.get("correlationId"));

        try {
            Long orcamentoId = microserviceGateway.criarOrcamento(ordemId, clienteId, valorEstimado);
            saga.setOrcamentoId(orcamentoId);
            saga.setStatus(SagaStatus.ORCAMENTO_EM_ANDAMENTO);
            saga.setMensagem("Nova ordem recebida. Orcamento " + orcamentoId + " criado para aprovação.");
            saga.registrarEtapa("OS_CRIADA");
            saga.registrarEtapa("ORCAMENTO_GERADO");
            microserviceGateway.atualizarStatusOs(ordemId, "AGUARDANDO_APROVACAO");
            eventBus.publish("os.criada", "OS " + ordemId + " iniciada no fluxo de orcamento");
            incrementCounter("saga.iniciadas");
        } catch (Exception ex) {
            log.error("Falha ao criar orçamento para OS {}. Executando compensação.", ordemId, ex);
            compensarOsComSeguranca(ordemId);
            saga.setStatus(SagaStatus.COMPENSADA);
            saga.setMensagem("Falha ao gerar orçamento (" + ex.getMessage() + "). OS cancelada por compensação.");
            saga.registrarEtapa("FALHA_ORCAMENTO");
            saga.registrarEtapa("COMPENSACAO_OS_CANCELADA");
            eventBus.publish("saga.compensada", "OS " + ordemId + " compensada após falha no orçamento");
            incrementCounter("saga.compensadas");
        }
        return repository.save(saga);
    }

    public SagaEntity avancarEtapa(Long ordemId, String evento) {
        SagaEntity saga = buscarPorOrdemId(ordemId);

        return switch (evento) {
            case "ORCAMENTO_APROVADO", "ORCAMENTO_PAGO" -> processarAprovacaoOuPagamento(saga, evento);
            case "ORCAMENTO_REJEITADO" -> compensarPorRejeicao(saga);
            case "EXECUCAO_FINALIZADA", "EXECUCAO_ENTREGUE" -> finalizarSaga(saga, evento);
            case "EXECUCAO_FALHOU" -> compensarPorFalhaExecucao(saga, "Falha reportada na execução");
            default -> {
                saga.setMensagem("Evento recebido: " + evento + ". Ainda aguardando transição de estado.");
                saga.registrarEtapa("EVENTO_" + evento);
                yield repository.save(saga);
            }
        };
    }

    @Transactional(readOnly = true)
    public SagaEntity buscarPorOrdemId(Long ordemId) {
        return repository.findByOrdemId(ordemId)
                .orElseThrow(() -> new IllegalArgumentException("Saga não encontrada para a ordem " + ordemId));
    }

    @Transactional(readOnly = true)
    public List<SagaEntity> listarTodas() {
        return repository.findAll();
    }

    private SagaEntity processarAprovacaoOuPagamento(SagaEntity saga, String evento) {
        try {
            String clienteId = saga.getClienteId() != null ? saga.getClienteId() : "cliente-padrao";
            String execucaoId = microserviceGateway.criarExecucao(
                    saga.getOrdemId(), clienteId, "Execução orquestrada OS #" + saga.getOrdemId());
            saga.setExecucaoId(execucaoId);
            saga.setStatus(SagaStatus.EXECUCAO_EM_ANDAMENTO);
            saga.setMensagem("Orcamento aprovado. Proxima etapa: execucao da ordem de servico.");
            saga.registrarEtapa(evento);
            saga.registrarEtapa("EXECUCAO_INICIADA");
            microserviceGateway.atualizarStatusOs(saga.getOrdemId(), "EM_EXECUCAO");
            eventBus.publish("orcamento.aprovado", "OS " + saga.getOrdemId() + " aprovada e enviada para execucao");
            return repository.save(saga);
        } catch (Exception ex) {
            log.error("Falha ao enviar OS {} para execução. Executando rollback financeiro e da OS.", saga.getOrdemId(), ex);
            return compensarPorFalhaExecucao(saga, ex.getMessage());
        }
    }

    private SagaEntity compensarPorRejeicao(SagaEntity saga) {
        compensarOsComSeguranca(saga.getOrdemId());
        saga.setStatus(SagaStatus.CANCELADA);
        saga.setMensagem("Orçamento rejeitado. A saga foi compensada para evitar execução inconsistente.");
        saga.registrarEtapa("ORCAMENTO_REJEITADO");
        saga.registrarEtapa("COMPENSACAO_OS_CANCELADA");
        eventBus.publish("orcamento.rejeitado", "OS " + saga.getOrdemId() + " foi compensada");
        incrementCounter("saga.compensadas");
        return repository.save(saga);
    }

    private SagaEntity compensarPorFalhaExecucao(SagaEntity saga, String motivo) {
        try {
            microserviceGateway.compensarOrcamento(saga.getOrcamentoId());
        } catch (Exception ex) {
            log.warn("Falha ao compensar orçamento {} da OS {}: {}", saga.getOrcamentoId(), saga.getOrdemId(), ex.getMessage());
        }
        compensarOsComSeguranca(saga.getOrdemId());
        saga.setStatus(SagaStatus.COMPENSADA);
        saga.setMensagem("Falha na execução (" + motivo + "). Orçamento estornado e OS cancelada.");
        saga.registrarEtapa("FALHA_EXECUCAO");
        saga.registrarEtapa("COMPENSACAO_ORCAMENTO_ESTORNADO");
        saga.registrarEtapa("COMPENSACAO_OS_CANCELADA");
        eventBus.publish("saga.compensada", "OS " + saga.getOrdemId() + " compensada após falha na execução");
        incrementCounter("saga.compensadas");
        return repository.save(saga);
    }

    private SagaEntity finalizarSaga(SagaEntity saga, String evento) {
        String novoStatusOs = "EXECUCAO_ENTREGUE".equals(evento) ? "ENTREGUE" : "FINALIZADA";
        microserviceGateway.atualizarStatusOs(saga.getOrdemId(), novoStatusOs);
        saga.setStatus(SagaStatus.FINALIZADA);
        saga.setMensagem("Execução concluída com sucesso. Ordem finalizada e entregue ao cliente.");
        saga.registrarEtapa(evento);
        saga.registrarEtapa("SAGA_FINALIZADA");
        eventBus.publish("execucao.finalizada", "OS " + saga.getOrdemId() + " concluída com sucesso");
        incrementCounter("saga.finalizadas");
        return repository.save(saga);
    }

    private void compensarOsComSeguranca(Long ordemId) {
        try {
            microserviceGateway.cancelarOs(ordemId);
        } catch (Exception ex) {
            log.warn("Falha ao cancelar OS {} durante compensação: {}", ordemId, ex.getMessage());
        }
    }

    private void incrementCounter(String name) {
        if (meterRegistry != null) {
            Counter.builder(name).register(meterRegistry).increment();
        }
    }
}
