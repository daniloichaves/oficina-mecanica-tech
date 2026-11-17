package com.oficina.executionservice.infrastructure.persistence;

import com.oficina.executionservice.domain.entity.Execucao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExecucaoRepository extends MongoRepository<Execucao, String> {
    List<Execucao> findByOrdemId(Long ordemId);
}
