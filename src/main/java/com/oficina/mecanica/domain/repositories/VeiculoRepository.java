package com.oficina.mecanica.domain.repositories;

import com.oficina.mecanica.domain.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Port hexagonal: implementado pelo adapter JPA na infraestrutura.
 */
public interface VeiculoRepository {
    Veiculo save(Veiculo veiculo);
    Optional<Veiculo> findById(Long id);
    List<Veiculo> findAll();
    Page<Veiculo> findAll(Pageable pageable);
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    List<Veiculo> findByClienteId(Long clienteId);
    boolean existsById(Long id);
    void deleteById(Long id);
}
