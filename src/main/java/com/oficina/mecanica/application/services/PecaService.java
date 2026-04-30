package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.PecaDTO;
import com.oficina.mecanica.domain.entities.Peca;
import com.oficina.mecanica.infrastructure.persistence.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PecaService {
    
    private final PecaRepository pecaRepository;
    
    public PecaDTO criar(PecaDTO dto) {
        Peca peca = Peca.builder()
            .nome(dto.getNome())
            .descricao(dto.getDescricao())
            .valor(dto.getValor())
            .quantidadeEstoque(dto.getQuantidadeEstoque())
            .build();
        peca = pecaRepository.save(peca);
        return toDTO(peca);
    }
    
    public PecaDTO atualizar(Long id, PecaDTO dto) {
        Peca peca = pecaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        
        peca.setNome(dto.getNome());
        peca.setDescricao(dto.getDescricao());
        peca.setValor(dto.getValor());
        peca.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        
        peca = pecaRepository.save(peca);
        return toDTO(peca);
    }
    
    public void deletar(Long id) {
        if (!pecaRepository.existsById(id)) {
            throw new IllegalArgumentException("Peça não encontrada");
        }
        pecaRepository.deleteById(id);
    }
    
    public PecaDTO atualizarEstoque(Long id, Integer quantidade) {
        Peca peca = pecaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        
        if (quantidade >= 0) {
            peca.aumentarEstoque(quantidade);
        } else {
            peca.diminuirEstoque(Math.abs(quantidade));
        }
        
        peca = pecaRepository.save(peca);
        return toDTO(peca);
    }
    
    @Transactional(readOnly = true)
    public PecaDTO buscarPorId(Long id) {
        Peca peca = pecaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        return toDTO(peca);
    }
    
    @Transactional(readOnly = true)
    public List<PecaDTO> listarTodos() {
        return pecaRepository.findAll(Pageable.unpaged()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<PecaDTO> listarTodos(Pageable pageable) {
        return pecaRepository.findAll(pageable)
            .map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public List<PecaDTO> listarEstoqueBaixo(Integer limite) {
        return pecaRepository.findByQuantidadeEstoqueLessThan(limite).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private PecaDTO toDTO(Peca peca) {
        return PecaDTO.builder()
            .id(peca.getId())
            .nome(peca.getNome())
            .descricao(peca.getDescricao())
            .valor(peca.getValor())
            .quantidadeEstoque(peca.getQuantidadeEstoque())
            .build();
    }
}
