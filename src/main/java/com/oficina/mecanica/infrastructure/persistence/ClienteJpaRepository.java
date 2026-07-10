package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.domain.repositories.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface ClienteJpaRepository
        extends JpaRepository<Cliente, Long>, ClienteRepository {
}
