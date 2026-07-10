package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Veiculo;
import com.oficina.mecanica.domain.repositories.VeiculoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface VeiculoJpaRepository
        extends JpaRepository<Veiculo, Long>, VeiculoRepository {
}
