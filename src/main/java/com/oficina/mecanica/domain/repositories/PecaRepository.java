package com.oficina.mecanica.domain.repositories;

import com.oficina.mecanica.domain.entities.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Port hexagonal: implementado pelo adapter JPA na infraestrutura.
 */
public interface PecaRepository {
    Peca save(Peca peca);
    Optional<Peca> findById(Long id);
    List<Peca> findAll();
    Page<Peca> findAll(Pageable pageable);
    List<Peca> findByQuantidadeEstoqueLessThan(Integer quantidade);
    boolean existsById(Long id);
    void deleteById(Long id);
}
