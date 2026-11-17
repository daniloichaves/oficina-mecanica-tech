package com.oficina.executionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.executionservice.application.dto.AtualizarStatusRequest;
import com.oficina.executionservice.application.dto.CriarExecucaoRequest;
import com.oficina.executionservice.application.service.ExecucaoService;
import com.oficina.executionservice.domain.entity.Execucao;
import com.oficina.executionservice.domain.entity.StatusExecucao;
import com.oficina.executionservice.presentation.rest.ExecucaoController;
import com.oficina.executionservice.presentation.rest.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExecucaoController.class)
@Import(GlobalExceptionHandler.class)
class ExecucaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ExecucaoService execucaoService;

    private Execucao sample(StatusExecucao status) {
        return Execucao.builder().id("exec-1").ordemId(10L).clienteId("CLI-1")
                .descricaoTarefa("Freios").status(status).build();
    }

    @Test
    void deveExecutarEndpointsETratamentoDeErros() throws Exception {
        when(execucaoService.criar(any())).thenReturn(sample(StatusExecucao.AGUARDANDO));
        when(execucaoService.listarTodos()).thenReturn(List.of(sample(StatusExecucao.AGUARDANDO)));
        when(execucaoService.buscarPorId("exec-1")).thenReturn(sample(StatusExecucao.AGUARDANDO));
        when(execucaoService.buscarPorOrdemId(10L)).thenReturn(List.of(sample(StatusExecucao.AGUARDANDO)));
        when(execucaoService.iniciar("exec-1")).thenReturn(sample(StatusExecucao.EM_EXECUCAO));
        when(execucaoService.atualizarStatus(eq("exec-1"), any())).thenReturn(sample(StatusExecucao.EM_DIAGNOSTICO));
        when(execucaoService.finalizar("exec-1")).thenReturn(sample(StatusExecucao.FINALIZADA));
        when(execucaoService.entregar("exec-1")).thenReturn(sample(StatusExecucao.ENTREGUE));
        when(execucaoService.cancelar("exec-1")).thenReturn(sample(StatusExecucao.CANCELADA));

        mockMvc.perform(post("/api/execucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarExecucaoRequest(10L, "CLI-1", "Freios"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/execucoes")).andExpect(status().isOk());
        mockMvc.perform(get("/api/execucoes/exec-1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/execucoes/ordem/10")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/execucoes/exec-1/iniciar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/execucoes/exec-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AtualizarStatusRequest(StatusExecucao.EM_DIAGNOSTICO))))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/execucoes/exec-1/finalizar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/execucoes/exec-1/entregar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/execucoes/exec-1/cancelar")).andExpect(status().isOk());

        when(execucaoService.buscarPorId("404")).thenThrow(new IllegalArgumentException("Não encontrada"));
        mockMvc.perform(get("/api/execucoes/404")).andExpect(status().isNotFound());

        when(execucaoService.finalizar("err")).thenThrow(new IllegalStateException("Status inválido"));
        mockMvc.perform(patch("/api/execucoes/err/finalizar")).andExpect(status().isConflict());

        mockMvc.perform(post("/api/execucoes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
