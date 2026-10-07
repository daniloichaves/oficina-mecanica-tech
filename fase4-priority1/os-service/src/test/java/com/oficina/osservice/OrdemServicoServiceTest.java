package com.oficina.osservice;

import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.infrastructure.event.NoOpOsEventPublisher;
import com.oficina.osservice.infrastructure.persistence.OrdemServicoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({OrdemServicoService.class, NoOpOsEventPublisher.class})
class OrdemServicoServiceTest {

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Test
    void deveCriarOrdemComStatusRecebida() {
        CriarOrdemServicoRequest request = new CriarOrdemServicoRequest(
                "ABC-1234",
                "CLIENTE-001",
                "GARANTA",
                "FALHA NO FREIO"
        );

        OrdemServico ordem = ordemServicoService.criar(request);

        assertThat(ordem.getId()).isNotNull();
        assertThat(ordem.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(ordem.getPlacaVeiculo()).isEqualTo("ABC-1234");
        assertThat(ordemServicoRepository.count()).isEqualTo(1);
    }
}
