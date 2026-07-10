package com.oficina.mecanica.domain.repositories;

import com.oficina.mecanica.domain.entities.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Port hexagonal: implementado pelo adapter JPA na infraestrutura.
 */
public interface ServicoRepository {
    Servico save(Servico servico);
    Optional<Servico> findById(Long id);
    List<Servico> findAll();
    Page<Servico> findAll(Pageable pageable);
    boolean existsById(Long id);
    void deleteById(Long id);
}
