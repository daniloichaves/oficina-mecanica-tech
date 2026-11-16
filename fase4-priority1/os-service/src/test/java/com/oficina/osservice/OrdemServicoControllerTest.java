package com.oficina.osservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.osservice.application.dto.AtualizarStatusRequest;
import com.oficina.osservice.application.dto.CriarOrdemServicoRequest;
import com.oficina.osservice.application.service.OrdemServicoService;
import com.oficina.osservice.domain.entity.OrdemServico;
import com.oficina.osservice.domain.entity.StatusOrdemServico;
import com.oficina.osservice.presentation.rest.GlobalExceptionHandler;
import com.oficina.osservice.presentation.rest.OrdemServicoController;
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

@WebMvcTest(OrdemServicoController.class)
@Import(GlobalExceptionHandler.class)
class OrdemServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private OrdemServicoService ordemServicoService;

    private OrdemServico sample(StatusOrdemServico status) {
        return OrdemServico.builder()
                .id(1L).clienteId("CLI-1").nomeCliente("João")
                .placaVeiculo("ABC1234").descricaoDefeito("Freio")
                .status(status).build();
    }

    @Test
    void deveExecutarEndpointsPrincipaisETreatmentDeErros() throws Exception {
        when(ordemServicoService.criar(any())).thenReturn(sample(StatusOrdemServico.RECEBIDA));
        when(ordemServicoService.buscarPorId(1L)).thenReturn(sample(StatusOrdemServico.RECEBIDA));
        when(ordemServicoService.listarTodos()).thenReturn(List.of(sample(StatusOrdemServico.RECEBIDA)));
        when(ordemServicoService.buscarPorStatus(StatusOrdemServico.RECEBIDA))
                .thenReturn(List.of(sample(StatusOrdemServico.RECEBIDA)));
        when(ordemServicoService.atualizarStatus(eq(1L), any())).thenReturn(sample(StatusOrdemServico.EM_EXECUCAO));
        when(ordemServicoService.cancelar(1L)).thenReturn(sample(StatusOrdemServico.CANCELADA));

        mockMvc.perform(post("/api/os")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CriarOrdemServicoRequest("ABC1234", "CLI-1", "João", "Freio"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEBIDA"));

        mockMvc.perform(get("/api/os/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/os")).andExpect(status().isOk());
        mockMvc.perform(get("/api/os/status/RECEBIDA")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/os/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AtualizarStatusRequest(StatusOrdemServico.EM_EXECUCAO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
        mockMvc.perform(patch("/api/os/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        when(ordemServicoService.buscarPorId(99L)).thenThrow(new IllegalArgumentException("Não encontrada"));
        mockMvc.perform(get("/api/os/99")).andExpect(status().isNotFound());

        when(ordemServicoService.cancelar(2L)).thenThrow(new IllegalStateException("Já entregue"));
        mockMvc.perform(patch("/api/os/2/cancelar")).andExpect(status().isConflict());

        mockMvc.perform(post("/api/os")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
