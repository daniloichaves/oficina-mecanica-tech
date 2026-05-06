package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByClienteId(Long clienteId);
    List<OrdemServico> findByVeiculoId(Long veiculoId);
    List<OrdemServico> findByStatus(StatusOrdemServico status);
    Page<OrdemServico> findAll(Pageable pageable);
    
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (os.data_entrega - os.data_criacao)) / 60) FROM ordens_servico os WHERE os.data_entrega IS NOT NULL", nativeQuery = true)
    Double getTempoMedioExecucao();
}
