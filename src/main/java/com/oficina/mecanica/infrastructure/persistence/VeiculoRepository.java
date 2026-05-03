package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    List<Veiculo> findByClienteId(Long clienteId);
    Page<Veiculo> findAll(Pageable pageable);
}
