package com.oficina.executionservice.application.service;

import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.infrastructure.persistence.ExecucaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecucaoService {

    private final ExecucaoRepository repository;

    public Execucao criar(CriarExecucaoRequest request) {
        Execucao execucao = Execucao.builder()
                .ordemId(request.ordemId())
                .clienteId(request.clienteId())
                .descricaoTarefa(request.descricaoTarefa())
                .status(StatusExecucao.AGUARDANDO)
                .build();

        return repository.save(execucao);
    }

    @Transactional(readOnly = true)
    public Execucao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Execucao> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Execucao> buscarPorOrdemId(Long ordemId) {
        return repository.findByOrdemId(ordemId);
    }

    public Execucao iniciar(Long id) {
        Execucao execucao = buscarPorId(id);
        execucao.iniciar();
        return repository.save(execucao);
    }

    public Execucao atualizarStatus(Long id, AtualizarStatusRequest request) {
        Execucao execucao = buscarPorId(id);
        execucao.setStatus(request.status());
        return repository.save(execucao);
    }

    public Execucao finalizar(Long id) {
        Execucao execucao = buscarPorId(id);
        execucao.finalizar();
        return repository.save(execucao);
    }

    public Execucao entregar(Long id) {
        Execucao execucao = buscarPorId(id);
        execucao.entregar();
        return repository.save(execucao);
    }
}
