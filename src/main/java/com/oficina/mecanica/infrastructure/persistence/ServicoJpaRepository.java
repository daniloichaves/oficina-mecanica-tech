package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Servico;
import com.oficina.mecanica.domain.repositories.ServicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface ServicoJpaRepository
        extends JpaRepository<Servico, Long>, ServicoRepository {
}
