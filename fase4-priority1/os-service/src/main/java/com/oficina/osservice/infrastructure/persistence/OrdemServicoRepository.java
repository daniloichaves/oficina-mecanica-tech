package com.oficina.osservice.infrastructure.persistence;

import com.oficina.osservice.domain.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
}
