package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.*;
import com.oficina.mecanica.domain.entities.*;
import com.oficina.mecanica.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdemServicoService {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final PecaRepository pecaRepository;
    
    public OrdemServicoDTO criar(CriarOrdemServicoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        
        OrdemServico os = OrdemServico.builder()
            .cliente(cliente)
            .veiculo(veiculo)
            .status(StatusOrdemServico.RECEBIDA)
            .build();
        
        for (ItemServicoDTO itemDto : dto.getItensServico()) {
            Servico servico = servicoRepository.findById(itemDto.getServicoId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + itemDto.getServicoId()));
            
            ItemServico item = ItemServico.builder()
                .ordemServico(os)
                .servico(servico)
                .quantidade(itemDto.getQuantidade())
                .build();
            os.getItensServico().add(item);
        }
        
        if (dto.getItensPeca() != null) {
            for (ItemPecaDTO itemDto : dto.getItensPeca()) {
                Peca peca = pecaRepository.findById(itemDto.getPecaId())
                    .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + itemDto.getPecaId()));
                
                if (peca.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para peça: " + peca.getNome());
                }
                
                ItemPeca item = ItemPeca.builder()
                    .ordemServico(os)
                    .peca(peca)
                    .quantidade(itemDto.getQuantidade())
                    .build();
                os.getItensPeca().add(item);
                peca.diminuirEstoque(itemDto.getQuantidade());
                pecaRepository.save(peca);
            }
        }
        
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    public OrdemServicoDTO iniciarDiagnostico(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        os.iniciarDiagnostico();
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    public OrdemServicoDTO concluirDiagnostico(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        os.concluirDiagnostico();
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    public OrdemServicoDTO aprovarOrcamento(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        os.aprovarOrcamento();
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    public OrdemServicoDTO finalizar(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        os.finalizar();
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    public OrdemServicoDTO entregar(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        os.entregar();
        os = ordemServicoRepository.save(os);
        return toDTO(os);
    }
    
    @Transactional(readOnly = true)
    public OrdemServicoDTO buscarPorId(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
        return toDTO(os);
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarTodos() {
        return ordemServicoRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarPorCliente(Long clienteId) {
        return ordemServicoRepository.findByClienteId(clienteId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarPorVeiculo(Long veiculoId) {
        return ordemServicoRepository.findByVeiculoId(veiculoId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarPorStatus(StatusOrdemServico status) {
        return ordemServicoRepository.findByStatus(status).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Double getTempoMedioExecucao() {
        return ordemServicoRepository.getTempoMedioExecucao();
    }
    
    private OrdemServicoDTO toDTO(OrdemServico os) {
        List<ItemServicoDTO> itensServico = os.getItensServico().stream()
            .map(item -> ItemServicoDTO.builder()
                .id(item.getId())
                .servicoId(item.getServico().getId())
                .servicoNome(item.getServico().getNome())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .valorTotal(item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .build())
            .collect(Collectors.toList());
        
        List<ItemPecaDTO> itensPeca = os.getItensPeca().stream()
            .map(item -> ItemPecaDTO.builder()
                .id(item.getId())
                .pecaId(item.getPeca().getId())
                .pecaNome(item.getPeca().getNome())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .valorTotal(item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .build())
            .collect(Collectors.toList());
        
        return OrdemServicoDTO.builder()
            .id(os.getId())
            .clienteId(os.getCliente().getId())
            .clienteNome(os.getCliente().getNome())
            .veiculoId(os.getVeiculo().getId())
            .veiculoPlaca(os.getVeiculo().getPlacaFormatada())
            .status(os.getStatus())
            .valorTotal(os.getValorTotal())
            .orcamentoAprovado(os.getOrcamentoAprovado())
            .dataCriacao(os.getDataCriacao())
            .dataAtualizacao(os.getDataAtualizacao())
            .dataEntrega(os.getDataEntrega())
            .itensServico(itensServico)
            .itensPeca(itensPeca)
            .build();
    }
}
