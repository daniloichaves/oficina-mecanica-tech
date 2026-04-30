package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.ClienteDTO;
import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.infrastructure.persistence.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public ClienteDTO criar(ClienteDTO dto) {
        if (clienteRepository.existsByCpfCnpj(dto.getCpfCnpj().replaceAll("[^0-9]", ""))) {
            throw new IllegalArgumentException("Cliente com este CPF/CNPJ já existe");
        }
        Cliente cliente = Cliente.builder()
            .cpfCnpj(dto.getCpfCnpj())
            .nome(dto.getNome())
            .telefone(dto.getTelefone())
            .email(dto.getEmail())
            .endereco(dto.getEndereco())
            .build();
        cliente = clienteRepository.save(cliente);
        return toDTO(cliente);
    }
    
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        if (!cliente.getCpfCnpj().equals(dto.getCpfCnpj().replaceAll("[^0-9]", "")) &&
            clienteRepository.existsByCpfCnpj(dto.getCpfCnpj().replaceAll("[^0-9]", ""))) {
            throw new IllegalArgumentException("CPF/CNPJ já em uso por outro cliente");
        }
        
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEndereco(dto.getEndereco());
        
        cliente = clienteRepository.save(cliente);
        return toDTO(cliente);
    }
    
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return toDTO(cliente);
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO buscarPorCpfCnpj(String cpfCnpj) {
        Cliente cliente = clienteRepository.findByCpfCnpj(cpfCnpj.replaceAll("[^0-9]", ""))
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return toDTO(cliente);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private ClienteDTO toDTO(Cliente cliente) {
        return ClienteDTO.builder()
            .id(cliente.getId())
            .cpfCnpj(cliente.getCpfCnpjFormatado())
            .nome(cliente.getNome())
            .telefone(cliente.getTelefone())
            .email(cliente.getEmail())
            .endereco(cliente.getEndereco())
            .build();
    }
}
