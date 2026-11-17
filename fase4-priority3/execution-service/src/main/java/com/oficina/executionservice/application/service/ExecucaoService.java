package com.oficina.executionservice.application.service;

import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.infrastructure.event.ExecutionEventPublisher;
import com.oficina.executionservice.infrastructure.persistence.ExecucaoRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecucaoService {

    private final ExecucaoRepository repository;
    private final ExecutionEventPublisher eventPublisher;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    public Execucao criar(CriarExecucaoRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Execucao execucao = Execucao.builder()
                .id(UUID.randomUUID().toString())
                .ordemId(request.ordemId())
                .clienteId(request.clienteId())
                .descricaoTarefa(request.descricaoTarefa())
                .status(StatusExecucao.AGUARDANDO)
                .dataCriacao(now)
                .dataAtualizacao(now)
                .build();

        Execucao salva = repository.save(execucao);
        incrementCounter("execution.criadas");
        eventPublisher.publish("execucao.criada", salva);
        return salva;
    }

    public Execucao buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));
    }

    public List<Execucao> listarTodos() {
        return repository.findAll();
    }

    public List<Execucao> buscarPorOrdemId(Long ordemId) {
        return repository.findByOrdemId(ordemId);
    }

    public Execucao iniciar(String id) {
        Execucao execucao = buscarPorId(id);
        execucao.iniciar();
        Execucao salva = repository.save(execucao);
        incrementCounter("execution.iniciadas");
        eventPublisher.publish("execucao.iniciada", salva);
        return salva;
    }

    public Execucao atualizarStatus(String id, AtualizarStatusRequest request) {
        Execucao execucao = buscarPorId(id);
        execucao.setStatus(request.status());
        execucao.setDataAtualizacao(LocalDateTime.now());
        Execucao salva = repository.save(execucao);
        eventPublisher.publish("execucao.status.atualizado", salva);
        return salva;
    }

    public Execucao finalizar(String id) {
        Execucao execucao = buscarPorId(id);
        execucao.finalizar();
        Execucao salva = repository.save(execucao);
        incrementCounter("execution.finalizadas");
        eventPublisher.publish("execucao.finalizada", salva);
        return salva;
    }

    public Execucao entregar(String id) {
        Execucao execucao = buscarPorId(id);
        execucao.entregar();
        Execucao salva = repository.save(execucao);
        incrementCounter("execution.entregues");
        eventPublisher.publish("execucao.entregue", salva);
        return salva;
    }

    public Execucao cancelar(String id) {
        Execucao execucao = buscarPorId(id);
        execucao.cancelar();
        Execucao salva = repository.save(execucao);
        incrementCounter("execution.canceladas");
        eventPublisher.publish("execucao.cancelada", salva);
        return salva;
    }

    private void incrementCounter(String name) {
        if (meterRegistry != null) {
            Counter.builder(name).register(meterRegistry).increment();
        }
    }
}
