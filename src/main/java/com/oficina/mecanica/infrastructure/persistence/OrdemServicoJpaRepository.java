package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.repositories.OrdemServicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface OrdemServicoJpaRepository
        extends JpaRepository<OrdemServico, Long>, OrdemServicoRepository {

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (os.data_entrega - os.data_criacao)) / 60) FROM ordens_servico os WHERE os.data_entrega IS NOT NULL", nativeQuery = true)
    Double getTempoMedioExecucao();
}
