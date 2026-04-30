package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.VeiculoDTO;
import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.domain.entities.Veiculo;
import com.oficina.mecanica.infrastructure.persistence.ClienteRepository;
import com.oficina.mecanica.infrastructure.persistence.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VeiculoService {
    
    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    
    public VeiculoDTO criar(VeiculoDTO dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca().toUpperCase().replaceAll("[^A-Z0-9]", ""))) {
            throw new IllegalArgumentException("Veículo com esta placa já existe");
        }
        
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        Veiculo veiculo = Veiculo.builder()
            .placa(dto.getPlaca())
            .marca(dto.getMarca())
            .modelo(dto.getModelo())
            .ano(dto.getAno())
            .cliente(cliente)
            .build();
        
        veiculo = veiculoRepository.save(veiculo);
        return toDTO(veiculo);
    }
    
    public VeiculoDTO atualizar(Long id, VeiculoDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        
        if (!veiculo.getPlaca().equals(dto.getPlaca().toUpperCase().replaceAll("[^A-Z0-9]", "")) &&
            veiculoRepository.existsByPlaca(dto.getPlaca().toUpperCase().replaceAll("[^A-Z0-9]", ""))) {
            throw new IllegalArgumentException("Placa já em uso por outro veículo");
        }
        
        if (dto.getClienteId() != null && !dto.getClienteId().equals(veiculo.getCliente().getId())) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            veiculo.setCliente(cliente);
        }
        
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setAno(dto.getAno());
        
        veiculo = veiculoRepository.save(veiculo);
        return toDTO(veiculo);
    }
    
    public void deletar(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new IllegalArgumentException("Veículo não encontrado");
        }
        veiculoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return toDTO(veiculo);
    }
    
    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorPlaca(String placa) {
        Veiculo veiculo = veiculoRepository.findByPlaca(placa.toUpperCase().replaceAll("[^A-Z0-9]", ""))
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        return toDTO(veiculo);
    }
    
    @Transactional(readOnly = true)
    public List<VeiculoDTO> listarTodos() {
        return veiculoRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VeiculoDTO> listarPorCliente(Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private VeiculoDTO toDTO(Veiculo veiculo) {
        return VeiculoDTO.builder()
            .id(veiculo.getId())
            .placa(veiculo.getPlacaFormatada())
            .marca(veiculo.getMarca())
            .modelo(veiculo.getModelo())
            .ano(veiculo.getAno())
            .clienteId(veiculo.getCliente() != null ? veiculo.getCliente().getId() : null)
            .build();
    }
}
