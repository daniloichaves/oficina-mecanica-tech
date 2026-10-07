package com.oficina.billingservice.infrastructure.persistence;

import com.oficina.billingservice.domain.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    List<Orcamento> findByOrdemId(Long ordemId);
}
