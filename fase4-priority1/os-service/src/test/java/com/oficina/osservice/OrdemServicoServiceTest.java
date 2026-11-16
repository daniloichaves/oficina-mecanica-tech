package com.oficina.osservice;

import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.infrastructure.event.NoOpOsEventPublisher;
import com.oficina.osservice.infrastructure.persistence.OrdemServicoRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({OrdemServicoService.class, NoOpOsEventPublisher.class, SimpleMeterRegistry.class})
class OrdemServicoServiceTest {

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Test
    void deveCriarOrdemComStatusRecebida() {
        CriarOrdemServicoRequest request = new CriarOrdemServicoRequest(
                "ABC-1234", "CLIENTE-001", "GARANTA", "FALHA NO FREIO"
        );

        OrdemServico ordem = ordemServicoService.criar(request);

        assertThat(ordem.getId()).isNotNull();
        assertThat(ordem.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(ordem.getPlacaVeiculo()).isEqualTo("ABC-1234");
        assertThat(ordemServicoRepository.count()).isEqualTo(1);
    }

    @Test
    void deveBuscarListarEAtualizarStatus() {
        OrdemServico criada = ordemServicoService.criar(
                new CriarOrdemServicoRequest("XYZ9A88", "CLI-2", "Maria", "Troca de óleo"));

        assertThat(ordemServicoService.buscarPorId(criada.getId()).getNomeCliente()).isEqualTo("Maria");
        assertThat(ordemServicoService.listarTodos()).hasSize(1);
        assertThat(ordemServicoService.buscarPorStatus(StatusOrdemServico.RECEBIDA)).hasSize(1);

        OrdemServico atualizada = ordemServicoService.atualizarStatus(
                criada.getId(), new AtualizarStatusRequest(StatusOrdemServico.EM_EXECUCAO));
        assertThat(atualizada.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    void deveCancelarOrdemQuandoNaoEntregueELancarErroQuandoEntregue() {
        OrdemServico os = ordemServicoService.criar(
                new CriarOrdemServicoRequest("DEF5678", "CLI-3", "Pedro", "Suspensão"));
        OrdemServico cancelada = ordemServicoService.cancelar(os.getId());
        assertThat(cancelada.getStatus()).isEqualTo(StatusOrdemServico.CANCELADA);

        OrdemServico osEntregue = ordemServicoService.criar(
                new CriarOrdemServicoRequest("GHI1122", "CLI-4", "Ana", "Revisão"));
        ordemServicoService.atualizarStatus(osEntregue.getId(), new AtualizarStatusRequest(StatusOrdemServico.ENTREGUE));

        assertThatThrownBy(() -> ordemServicoService.cancelar(osEntregue.getId()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> ordemServicoService.buscarPorId(99999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
