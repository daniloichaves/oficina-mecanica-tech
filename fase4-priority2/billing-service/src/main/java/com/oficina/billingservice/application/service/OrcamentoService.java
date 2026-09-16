package com.oficina.billingservice.application.service;

import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.infrastructure.persistence.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrcamentoService {

    private final OrcamentoRepository repository;

    public Orcamento criar(CriarOrcamentoRequest request) {
        Orcamento orcamento = Orcamento.builder()
                .ordemId(request.ordemId())
                .clienteId(request.clienteId())
                .valor(request.valor())
                .status(StatusOrcamento.PENDENTE)
                .build();

        return repository.save(orcamento);
    }

    @Transactional(readOnly = true)
    public Orcamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Orcamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Orcamento> buscarPorOrdemId(Long ordemId) {
        return repository.findByOrdemId(ordemId);
    }

    public Orcamento aprovar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.aprovar();
        return repository.save(orcamento);
    }

    public Orcamento rejeitar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.rejeitar();
        return repository.save(orcamento);
    }

    public Orcamento pagar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.pagar();
        return repository.save(orcamento);
    }
}
