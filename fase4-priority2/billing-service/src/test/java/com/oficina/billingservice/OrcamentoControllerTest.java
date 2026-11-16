package com.oficina.billingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billingservice.application.dto.CriarOrcamentoRequest;
import com.oficina.billingservice.application.dto.MercadoPagoWebhookRequest;
import com.oficina.billingservice.application.service.OrcamentoService;
import com.oficina.billingservice.domain.entity.Orcamento;
import com.oficina.billingservice.domain.entity.StatusOrcamento;
import com.oficina.billingservice.presentation.rest.GlobalExceptionHandler;
import com.oficina.billingservice.presentation.rest.OrcamentoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrcamentoController.class)
@Import(GlobalExceptionHandler.class)
class OrcamentoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private OrcamentoService orcamentoService;

    private Orcamento sample(StatusOrcamento status) {
        return Orcamento.builder().id(1L).ordemId(10L).clienteId("CLI-1")
                .valor(new BigDecimal("450.00")).status(status).build();
    }

    @Test
    void deveExecutarEndpointsDeOrcamentoETratamentoDeErros() throws Exception {
        when(orcamentoService.criar(any())).thenReturn(sample(StatusOrcamento.PENDENTE));
        when(orcamentoService.listarTodos()).thenReturn(List.of(sample(StatusOrcamento.PENDENTE)));
        when(orcamentoService.buscarPorId(1L)).thenReturn(sample(StatusOrcamento.PENDENTE));
        when(orcamentoService.buscarPorOrdemId(10L)).thenReturn(List.of(sample(StatusOrcamento.PENDENTE)));
        when(orcamentoService.aprovar(1L)).thenReturn(sample(StatusOrcamento.APROVADO));
        when(orcamentoService.rejeitar(1L)).thenReturn(sample(StatusOrcamento.REJEITADO));
        when(orcamentoService.pagar(1L)).thenReturn(sample(StatusOrcamento.PAGO));
        when(orcamentoService.compensar(1L)).thenReturn(sample(StatusOrcamento.ESTORNADO));
        when(orcamentoService.processarWebhookMercadoPago(any())).thenReturn(sample(StatusOrcamento.PAGO));

        mockMvc.perform(post("/api/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CriarOrcamentoRequest(10L, "CLI-1", new BigDecimal("450.00")))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orcamentos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/orcamentos/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/orcamentos/ordem/10")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/orcamentos/1/aprovar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/orcamentos/1/rejeitar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/orcamentos/1/pagar")).andExpect(status().isOk());
        mockMvc.perform(patch("/api/orcamentos/1/compensar")).andExpect(status().isOk());
        mockMvc.perform(post("/api/orcamentos/webhook/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MercadoPagoWebhookRequest(1L, "mp-1", "approved"))))
                .andExpect(status().isOk());

        when(orcamentoService.buscarPorId(99L)).thenThrow(new IllegalArgumentException("Não encontrado"));
        mockMvc.perform(get("/api/orcamentos/99")).andExpect(status().isNotFound());

        when(orcamentoService.pagar(2L)).thenThrow(new IllegalStateException("Não aprovado"));
        mockMvc.perform(patch("/api/orcamentos/2/pagar")).andExpect(status().isConflict());

        mockMvc.perform(post("/api/orcamentos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
