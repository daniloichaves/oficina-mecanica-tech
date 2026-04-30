package com.oficina.mecanica.infrastructure.persistence;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByClienteId(Long clienteId);
    List<OrdemServico> findByVeiculoId(Long veiculoId);
    List<OrdemServico> findByStatus(StatusOrdemServico status);
    
    @Query("SELECT AVG(os.dataEntrega - os.dataCriacao) FROM OrdemServico os WHERE os.dataEntrega IS NOT NULL")
    Double getTempoMedioExecucao();
}
