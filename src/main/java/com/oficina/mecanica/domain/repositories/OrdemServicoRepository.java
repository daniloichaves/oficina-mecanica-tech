package com.oficina.mecanica.domain.repositories;

import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Port hexagonal: a aplicação depende desta interface; o adapter JPA
 * ({@code infrastructure.persistence.OrdemServicoJpaRepository}) a implementa.
 * ponytail: Page/Pageable (Spring Data) tolerados no port para não reescrever a
 * paginação do endpoint /paginado; trocar por VO próprio se sair do Spring.
 */
public interface OrdemServicoRepository {
    OrdemServico save(OrdemServico ordemServico);
    Optional<OrdemServico> findById(Long id);
    List<OrdemServico> findAll();
    Page<OrdemServico> findAll(Pageable pageable);
    List<OrdemServico> findByClienteId(Long clienteId);
    List<OrdemServico> findByVeiculoId(Long veiculoId);
    List<OrdemServico> findByStatus(StatusOrdemServico status);
    Double getTempoMedioExecucao();
}
