package com.oficina.executionservice.infrastructure.persistence;

import com.oficina.executionservice.domain.entity.Execucao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecucaoRepository extends JpaRepository<Execucao, Long> {
    List<Execucao> findByOrdemId(Long ordemId);
}
