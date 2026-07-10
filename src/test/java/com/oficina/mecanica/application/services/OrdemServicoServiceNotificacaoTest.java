package com.oficina.mecanica.application.services;

import com.oficina.mecanica.domain.entities.Cliente;
import com.oficina.mecanica.domain.entities.OrdemServico;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import com.oficina.mecanica.domain.entities.Veiculo;
import com.oficina.mecanica.domain.repositories.ClienteRepository;
import com.oficina.mecanica.domain.repositories.NotificacaoPort;
import com.oficina.mecanica.domain.repositories.OrdemServicoRepository;
import com.oficina.mecanica.domain.repositories.PecaRepository;
import com.oficina.mecanica.domain.repositories.ServicoRepository;
import com.oficina.mecanica.domain.repositories.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}
