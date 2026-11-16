package com.oficina.mecanica.application.services;

import com.oficina.mecanica.application.dto.CriarOrdemServicoDTO;
import com.oficina.mecanica.application.dto.ItemServicoDTO;
import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.Servico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import com.oficina.mecanica.domain.entities.Veiculo;
import com.oficina.mecanica.domain.repositories.ClienteRepository;
import com.oficina.mecanica.domain.repositories.NotificacaoPort;
import com.oficina.mecanica.domain.repositories.OrdemServicoRepository;
import com.oficina.mecanica.domain.repositories.PecaRepository;
import com.oficina.mecanica.domain.repositories.ServicoRepository;
import com.oficina.mecanica.domain.repositories.VeiculoRepository;
import com.oficina.mecanica.infrastructure.observability.OficinaMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceNotificacaoTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private PecaRepository pecaRepository;
    @Mock private NotificacaoPort notificacaoPort;

    @InjectMocks private OrdemServicoService service;

    private OrdemServico os(StatusOrdemServico status) {
        Cliente cliente = Cliente.builder().id(1L).nome("Ana").email("ana@ex.com").build();
        Veiculo veiculo = Veiculo.builder().id(1L).placa("ABC1234").build();
        return OrdemServico.builder().id(1L).cliente(cliente).veiculo(veiculo)
            .status(status).build();
    }

    private void mockRepositorio(OrdemServico os) {
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(os));
        when(ordemServicoRepository.save(any())).thenReturn(os);
    }

    @Test
    void deveNotificarAoIniciarDiagnostico() {
        OrdemServico os = os(StatusOrdemServico.RECEBIDA);
        mockRepositorio(os);

        service.iniciarDiagnostico(1L);

        verify(notificacaoPort).notificarMudancaStatus(os);
    }

    @Test
    void deveNotificarAoAprovarOrcamento() {
        OrdemServico os = os(StatusOrdemServico.AGUARDANDO_APROVACAO);
        mockRepositorio(os);

        service.aprovarOrcamento(1L);

        verify(notificacaoPort).notificarMudancaStatus(os);
    }

    @Test
    void deveNotificarAoRecusarOrcamento() {
        OrdemServico os = os(StatusOrdemServico.AGUARDANDO_APROVACAO);
        mockRepositorio(os);

        service.recusarOrcamento(1L);

        verify(notificacaoPort).notificarMudancaStatus(os);
    }

    @Test
    void deveNotificarAoEntregar() {
        OrdemServico os = os(StatusOrdemServico.FINALIZADA);
        mockRepositorio(os);

        service.entregar(1L);

        verify(notificacaoPort).notificarMudancaStatus(os);
    }

    @Test
    void deveRegistrarMetricasDeTransicaoEErroQuandoOsNaoEncontrada() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ReflectionTestUtils.setField(service, "oficinaMetrics", new OficinaMetrics(registry));

        OrdemServico os = os(StatusOrdemServico.RECEBIDA);
        mockRepositorio(os);
        service.iniciarDiagnostico(1L);
        assertEquals(1.0, registry.get("oficina.os.transicoes").tag("status", "EM_DIAGNOSTICO").counter().count());

        when(ordemServicoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.concluirDiagnostico(99L));
        assertEquals(1.0, registry.get("oficina.os.erros").tag("operacao", "concluirDiagnostico").counter().count());

        ReflectionTestUtils.setField(service, "oficinaMetrics", null);
        assertThrows(IllegalArgumentException.class, () -> service.finalizar(99L));

        Cliente cliente = Cliente.builder().id(1L).nome("Ana").email("ana@ex.com").build();
        Veiculo veiculo = Veiculo.builder().id(1L).placa("ABC1234").build();
        Servico servico = Servico.builder().id(1L).nome("Revisão").valor(new BigDecimal("100.00")).build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(ordemServicoRepository.save(any())).thenAnswer(inv -> {
            OrdemServico salvo = inv.getArgument(0);
            salvo.getItensServico().forEach(i -> i.setValorUnitario(i.getServico().getValor()));
            return salvo;
        });

        CriarOrdemServicoDTO dto = CriarOrdemServicoDTO.builder()
            .clienteId(1L)
            .veiculoId(1L)
            .itensServico(List.of(ItemServicoDTO.builder().servicoId(1L).quantidade(1).build()))
            .build();
        service.criar(dto);
    }
}
