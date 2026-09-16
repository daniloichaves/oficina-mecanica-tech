package com.oficina.saga.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SagaRepository extends JpaRepository<SagaEntity, Long> {
    Optional<SagaEntity> findByOrdemId(Long ordemId);
}
