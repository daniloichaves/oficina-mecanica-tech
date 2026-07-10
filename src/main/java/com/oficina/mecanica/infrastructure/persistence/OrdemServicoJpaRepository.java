package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.repositories.OrdemServicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter de persistência: Spring Data implementa o port em runtime.
 */
@Repository
public interface OrdemServicoJpaRepository
        extends JpaRepository<OrdemServico, Long>, OrdemServicoRepository {

    @Query("""
        SELECT os FROM OrdemServico os
        WHERE os.status NOT IN (com.oficina.mecanica.domain.entities.StatusOrdemServico.FINALIZADA,
                                com.oficina.mecanica.domain.entities.StatusOrdemServico.ENTREGUE,
                                com.oficina.mecanica.domain.entities.StatusOrdemServico.CANCELADA)
        ORDER BY CASE os.status
            WHEN com.oficina.mecanica.domain.entities.StatusOrdemServico.EM_EXECUCAO THEN 1
            WHEN com.oficina.mecanica.domain.entities.StatusOrdemServico.AGUARDANDO_APROVACAO THEN 2
            WHEN com.oficina.mecanica.domain.entities.StatusOrdemServico.EM_DIAGNOSTICO THEN 3
            ELSE 4
        END, os.dataCriacao ASC
        """)
    List<OrdemServico> findAtivasOrdenadas();

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (os.data_entrega - os.data_criacao)) / 60) FROM ordens_servico os WHERE os.data_entrega IS NOT NULL", nativeQuery = true)
    Double getTempoMedioExecucao();
}
