package com.oficina.executionservice;

import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.application.service.ExecucaoService;
import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.infrastructure.event.ExecutionEventPublisher;
import com.oficina.executionservice.infrastructure.persistence.ExecucaoRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecucaoServiceTest {

    @Mock private ExecucaoRepository repository;
    @Mock private ExecutionEventPublisher eventPublisher;

    private ExecucaoService execucaoService;

    @BeforeEach
    void setUp() {
        execucaoService = new ExecucaoService(repository, eventPublisher);
        ReflectionTestUtils.setField(execucaoService, "meterRegistry", new SimpleMeterRegistry());
        lenient().when(repository.save(any(Execucao.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveExecutarCicloCompletoDaExecucaoNoSql() {
        Execucao criada = execucaoService.criar(new CriarExecucaoRequest(10L, "CLIENTE-10", "Troca de pastilha"));
        assertThat(criada.getId()).isNotBlank();
        assertThat(criada.getStatus()).isEqualTo(StatusExecucao.AGUARDANDO);
        verify(eventPublisher).publish("execucao.criada", criada);

        when(repository.findById(criada.getId())).thenReturn(Optional.of(criada));
        when(repository.findAll()).thenReturn(List.of(criada));
        when(repository.findByOrdemId(10L)).thenReturn(List.of(criada));

        assertThat(execucaoService.listarTodos()).hasSize(1);
        assertThat(execucaoService.buscarPorOrdemId(10L)).hasSize(1);

        Execucao iniciada = execucaoService.iniciar(criada.getId());
        assertThat(iniciada.getStatus()).isEqualTo(StatusExecucao.EM_EXECUCAO);

        Execucao finalizada = execucaoService.finalizar(criada.getId());
        assertThat(finalizada.getStatus()).isEqualTo(StatusExecucao.FINALIZADA);

        Execucao entregue = execucaoService.entregar(criada.getId());
        assertThat(entregue.getStatus()).isEqualTo(StatusExecucao.ENTREGUE);

        assertThatThrownBy(() -> execucaoService.cancelar(criada.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveValidarTransicoesInvalidasECancelamento() {
        Execucao exec = Execucao.builder().id("exec-1").ordemId(20L).clienteId("CLI-20")
                .descricaoTarefa("Alinhamento").status(StatusExecucao.AGUARDANDO).build();
        when(repository.findById("exec-1")).thenReturn(Optional.of(exec));
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> execucaoService.buscarPorId("inexistente")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> execucaoService.finalizar("exec-1")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> execucaoService.entregar("exec-1")).isInstanceOf(IllegalStateException.class);

        execucaoService.atualizarStatus("exec-1", new AtualizarStatusRequest(StatusExecucao.EM_DIAGNOSTICO));
        assertThat(exec.getStatus()).isEqualTo(StatusExecucao.EM_DIAGNOSTICO);

        execucaoService.cancelar("exec-1");
        assertThat(exec.getStatus()).isEqualTo(StatusExecucao.CANCELADA);
        assertThatThrownBy(() -> execucaoService.iniciar("exec-1")).isInstanceOf(IllegalStateException.class);
    }
}
