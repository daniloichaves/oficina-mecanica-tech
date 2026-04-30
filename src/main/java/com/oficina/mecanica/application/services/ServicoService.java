package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.ServicoDTO;
import com.oficina.mecanica.domain.entities.Servico;
import com.oficina.mecanica.infrastructure.persistence.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicoService {
    
    private final ServicoRepository servicoRepository;
    
    public ServicoDTO criar(ServicoDTO dto) {
        Servico servico = Servico.builder()
            .nome(dto.getNome())
            .descricao(dto.getDescricao())
            .valor(dto.getValor())
            .tempoEstimadoMinutos(dto.getTempoEstimadoMinutos())
            .build();
        servico = servicoRepository.save(servico);
        return toDTO(servico);
    }
    
    public ServicoDTO atualizar(Long id, ServicoDTO dto) {
        Servico servico = servicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setValor(dto.getValor());
        servico.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());
        
        servico = servicoRepository.save(servico);
        return toDTO(servico);
    }
    
    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Serviço não encontrado");
        }
        servicoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public ServicoDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return toDTO(servico);
    }
    
    @Transactional(readOnly = true)
    public List<ServicoDTO> listarTodos() {
        return servicoRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private ServicoDTO toDTO(Servico servico) {
        return ServicoDTO.builder()
            .id(servico.getId())
            .nome(servico.getNome())
            .descricao(servico.getDescricao())
            .valor(servico.getValor())
            .tempoEstimadoMinutos(servico.getTempoEstimadoMinutos())
            .build();
    }
}
