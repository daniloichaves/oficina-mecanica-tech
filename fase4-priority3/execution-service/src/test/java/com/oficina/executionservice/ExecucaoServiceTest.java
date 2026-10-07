package com.oficina.executionservice;

import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.application.service.ExecucaoService;
import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.infrastructure.persistence.ExecucaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ExecucaoService.class)
class ExecucaoServiceTest {

    @Autowired
    private ExecucaoService execucaoService;

    @Autowired
    private ExecucaoRepository execucaoRepository;

    @Test
    void deveCriarExecucaoEmStatusAguardando() {
        CriarExecucaoRequest request = new CriarExecucaoRequest(10L, "CLIENTE-10", "Troca de pastilha de freio");

        Execucao execucao = execucaoService.criar(request);

        assertThat(execucao.getId()).isNotNull();
        assertThat(execucao.getStatus()).isEqualTo(StatusExecucao.AGUARDANDO);
        assertThat(execucao.getDescricaoTarefa()).isEqualTo("Troca de pastilha de freio");
        assertThat(execucaoRepository.count()).isEqualTo(1);
    }

    @Test
    void deveIniciarExecucao() {
        Execucao execucao = execucaoService.criar(new CriarExecucaoRequest(20L, "CLIENTE-20", "Alinhamento e balanceamento"));

        Execucao atualizada = execucaoService.iniciar(execucao.getId());

        assertThat(atualizada.getStatus()).isEqualTo(StatusExecucao.EM_EXECUCAO);
    }
}
