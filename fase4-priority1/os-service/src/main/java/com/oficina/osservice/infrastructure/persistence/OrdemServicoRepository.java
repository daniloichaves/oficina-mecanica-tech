package com.oficina.osservice.infrastructure.persistence;

import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByStatus(StatusOrdemServico status);
}
