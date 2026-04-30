package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
