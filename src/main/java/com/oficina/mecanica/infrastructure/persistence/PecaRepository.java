package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {
    List<Peca> findByQuantidadeEstoqueLessThan(Integer quantidade);
    Page<Peca> findAll(Pageable pageable);
}
