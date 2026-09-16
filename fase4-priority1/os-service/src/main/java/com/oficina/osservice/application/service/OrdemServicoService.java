package com.oficina.osservice.application.service;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.infrastructure.event.OsEventPublisher;
import com.oficina.osservice.infrastructure.persistence.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final OsEventPublisher osEventPublisher;

    public OrdemServico criar(CriarOrdemServicoRequest request) {
        OrdemServico ordem = OrdemServico.builder()
                .clienteId(request.clienteId())
                .nomeCliente(request.nomeCliente())
                .placaVeiculo(request.placaVeiculo())
                .descricaoDefeito(request.descricaoDefeito())
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        OrdemServico salvo = repository.save(ordem);
        osEventPublisher.publishStatusChanged(String.valueOf(salvo.getId()), salvo.getStatus());
        return salvo;
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    public OrdemServico atualizarStatus(Long id, AtualizarStatusRequest request) {
        OrdemServico ordem = buscarPorId(id);
        ordem.setStatus(request.status());
        OrdemServico salvo = repository.save(ordem);
        osEventPublisher.publishStatusChanged(String.valueOf(salvo.getId()), salvo.getStatus());
        return salvo;
    }
}
