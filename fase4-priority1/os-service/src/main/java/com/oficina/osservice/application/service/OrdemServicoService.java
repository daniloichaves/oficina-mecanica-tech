package com.oficina.osservice.application.service;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.infrastructure.event.OsEventPublisher;
import com.oficina.osservice.infrastructure.persistence.OrdemServicoRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final OsEventPublisher osEventPublisher;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    public OrdemServico criar(CriarOrdemServicoRequest request) {
        OrdemServico ordem = OrdemServico.builder()
                .clienteId(request.clienteId())
                .nomeCliente(request.nomeCliente())
                .placaVeiculo(request.placaVeiculo())
                .descricaoDefeito(request.descricaoDefeito())
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        OrdemServico salvo = repository.save(ordem);
        incrementCounter("os.service.criadas", "status", salvo.getStatus().name());
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

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorStatus(StatusOrdemServico status) {
        return repository.findByStatus(status);
    }

    public OrdemServico atualizarStatus(Long id, AtualizarStatusRequest request) {
        OrdemServico ordem = buscarPorId(id);
        ordem.setStatus(request.status());
        OrdemServico salvo = repository.save(ordem);
        incrementCounter("os.service.transicoes", "status", salvo.getStatus().name());
        osEventPublisher.publishStatusChanged(String.valueOf(salvo.getId()), salvo.getStatus());
        return salvo;
    }

    public OrdemServico cancelar(Long id) {
        OrdemServico ordem = buscarPorId(id);
        if (ordem.getStatus() == StatusOrdemServico.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar uma ordem já entregue");
        }
        ordem.setStatus(StatusOrdemServico.CANCELADA);
        OrdemServico salvo = repository.save(ordem);
        incrementCounter("os.service.canceladas", "status", salvo.getStatus().name());
        osEventPublisher.publishStatusChanged(String.valueOf(salvo.getId()), salvo.getStatus());
        return salvo;
    }

    private void incrementCounter(String name, String tagKey, String tagValue) {
        if (meterRegistry != null) {
            Counter.builder(name).tag(tagKey, tagValue).register(meterRegistry).increment();
        }
    }
}
