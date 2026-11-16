package com.oficina.saga;

import com.oficina.saga.application.SagaEntity;
import com.oficina.saga.application.SagaOrchestratorService;
import com.oficina.saga.application.SagaStatus;
import com.oficina.saga.presentation.GlobalExceptionHandler;
import com.oficina.saga.presentation.SagaController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SagaController.class)
@Import(GlobalExceptionHandler.class)
class SagaControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private SagaOrchestratorService service;

    private SagaEntity sample(SagaStatus status) {
        SagaEntity entity = new SagaEntity();
        entity.setId(1L);
        entity.setOrdemId(10L);
        entity.setStatus(status);
        entity.setMensagem("ok");
        return entity;
    }

    @Test
    void deveExecutarEndpointsDeSagaETratamentoDeErros() throws Exception {
        when(service.iniciarNovaOrdem(10L)).thenReturn(sample(SagaStatus.ORCAMENTO_EM_ANDAMENTO));
        when(service.avancarEtapa(10L, "ORCAMENTO_APROVADO")).thenReturn(sample(SagaStatus.EXECUCAO_EM_ANDAMENTO));
        when(service.buscarPorOrdemId(10L)).thenReturn(sample(SagaStatus.EXECUCAO_EM_ANDAMENTO));
        when(service.listarTodas()).thenReturn(List.of(sample(SagaStatus.EXECUCAO_EM_ANDAMENTO)));

        mockMvc.perform(post("/api/sagas/ordens/10/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ORCAMENTO_EM_ANDAMENTO"));

        mockMvc.perform(post("/api/sagas/ordens/10/eventos").param("evento", "ORCAMENTO_APROVADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXECUCAO_EM_ANDAMENTO"));

        mockMvc.perform(get("/api/sagas/ordens/10")).andExpect(status().isOk());
        mockMvc.perform(get("/api/sagas")).andExpect(status().isOk());

        when(service.buscarPorOrdemId(404L)).thenThrow(new IllegalArgumentException("Saga não encontrada"));
        mockMvc.perform(get("/api/sagas/ordens/404")).andExpect(status().isNotFound());

        when(service.avancarEtapa(10L, "INVALID")).thenThrow(new IllegalStateException("Conflito"));
        mockMvc.perform(post("/api/sagas/ordens/10/eventos").param("evento", "INVALID"))
                .andExpect(status().isConflict());
    }
}
