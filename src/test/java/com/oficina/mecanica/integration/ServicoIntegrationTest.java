package com.oficina.mecanica.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import com.oficina.mecanica.application.dto.ServicoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ServicoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ServicoDTO servicoDTO;

    @BeforeEach
    void setUp() {
        servicoDTO = ServicoDTO.builder()
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo do motor")
                .valor(new BigDecimal("150.00"))
                .tempoEstimadoMinutos(30)
                .build();
    }

    @Test
    void testCriarServico() throws Exception {
        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"))
                .andExpect(jsonPath("$.descricao").value("Troca completa de óleo do motor"))
                .andExpect(jsonPath("$.valor").value(150.00))
                .andExpect(jsonPath("$.tempoEstimadoMinutos").value(30));
    }

    @Test
    void testBuscarServicoPorId() throws Exception {
        String response = mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ServicoDTO createdServico = objectMapper.readValue(response, ServicoDTO.class);

        mockMvc.perform(get("/api/servicos/" + createdServico.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdServico.getId()))
                .andExpect(jsonPath("$.nome").value("Troca de Óleo"));
    }

    @Test
    void testListarTodosServicos() throws Exception {
        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andExpect(status().isCreated());

        ServicoDTO servico2 = ServicoDTO.builder()
                .nome("Alinhamento")
                .descricao("Alinhamento de direção")
                .valor(new BigDecimal("80.00"))
                .tempoEstimadoMinutos(45)
                .build();

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servico2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testAtualizarServico() throws Exception {
        String response = mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ServicoDTO createdServico = objectMapper.readValue(response, ServicoDTO.class);

        ServicoDTO updatedDTO = ServicoDTO.builder()
                .nome("Troca de Óleo Premium")
                .descricao("Troca completa de óleo sintético")
                .valor(new BigDecimal("200.00"))
                .tempoEstimadoMinutos(35)
                .build();

        mockMvc.perform(put("/api/servicos/" + createdServico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdServico.getId()))
                .andExpect(jsonPath("$.nome").value("Troca de Óleo Premium"))
                .andExpect(jsonPath("$.valor").value(200.00));
    }

    @Test
    void testDeletarServico() throws Exception {
        String response = mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ServicoDTO createdServico = objectMapper.readValue(response, ServicoDTO.class);

        mockMvc.perform(delete("/api/servicos/" + createdServico.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/servicos/" + createdServico.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidacaoCriarServicoSemNome() throws Exception {
        ServicoDTO invalidDTO = ServicoDTO.builder()
                .descricao("Troca de óleo")
                .valor(new BigDecimal("150.00"))
                .tempoEstimadoMinutos(30)
                .build();

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarServicoValorZero() throws Exception {
        ServicoDTO invalidDTO = ServicoDTO.builder()
                .nome("Troca de Óleo")
                .descricao("Troca de óleo")
                .valor(new BigDecimal("0.00"))
                .tempoEstimadoMinutos(30)
                .build();

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarServicoTempoNegativo() throws Exception {
        ServicoDTO invalidDTO = ServicoDTO.builder()
                .nome("Troca de Óleo")
                .descricao("Troca de óleo")
                .valor(new BigDecimal("150.00"))
                .tempoEstimadoMinutos(-10)
                .build();

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
