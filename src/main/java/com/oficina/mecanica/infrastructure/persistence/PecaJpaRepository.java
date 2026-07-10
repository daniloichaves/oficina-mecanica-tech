package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Peca;
import com.oficina.mecanica.domain.repositories.PecaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface PecaJpaRepository
        extends JpaRepository<Peca, Long>, PecaRepository {
}
