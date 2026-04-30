package com.oficina.mecanica.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import com.oficina.mecanica.application.dto.PecaDTO;
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
public class PecaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PecaDTO pecaDTO;

    @BeforeEach
    void setUp() {
        pecaDTO = PecaDTO.builder()
                .nome("Filtro de Óleo")
                .descricao("Filtro de óleo para motor 1.6")
                .valor(new BigDecimal("45.00"))
                .quantidadeEstoque(20)
                .build();
    }

    @Test
    void testCriarPeca() throws Exception {
        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Filtro de Óleo"))
                .andExpect(jsonPath("$.descricao").value("Filtro de óleo para motor 1.6"))
                .andExpect(jsonPath("$.valor").value(45.00))
                .andExpect(jsonPath("$.quantidadeEstoque").value(20));
    }

    @Test
    void testBuscarPecaPorId() throws Exception {
        String response = mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PecaDTO createdPeca = objectMapper.readValue(response, PecaDTO.class);

        mockMvc.perform(get("/api/pecas/" + createdPeca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPeca.getId()))
                .andExpect(jsonPath("$.nome").value("Filtro de Óleo"));
    }

    @Test
    void testListarTodasPecas() throws Exception {
        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andExpect(status().isCreated());

        PecaDTO peca2 = PecaDTO.builder()
                .nome("Pastilha de Freio")
                .descricao("Pastilha de freio dianteira")
                .valor(new BigDecimal("120.00"))
                .quantidadeEstoque(15)
                .build();

        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peca2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pecas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testListarEstoqueBaixo() throws Exception {
        PecaDTO pecaBaixoEstoque = PecaDTO.builder()
                .nome("Parafuso Especial")
                .descricao("Parafuso para fixação")
                .valor(new BigDecimal("5.00"))
                .quantidadeEstoque(3)
                .build();

        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaBaixoEstoque)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pecas/estoque-baixo?limite=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].quantidadeEstoque", lessThanOrEqualTo(5)));
    }

    @Test
    void testAtualizarPeca() throws Exception {
        String response = mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PecaDTO createdPeca = objectMapper.readValue(response, PecaDTO.class);

        PecaDTO updatedDTO = PecaDTO.builder()
                .nome("Filtro de Óleo Premium")
                .descricao("Filtro de óleo de alta qualidade")
                .valor(new BigDecimal("65.00"))
                .quantidadeEstoque(25)
                .build();

        mockMvc.perform(put("/api/pecas/" + createdPeca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPeca.getId()))
                .andExpect(jsonPath("$.nome").value("Filtro de Óleo Premium"))
                .andExpect(jsonPath("$.valor").value(65.00));
    }

    @Test
    void testAtualizarEstoque() throws Exception {
        String response = mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PecaDTO createdPeca = objectMapper.readValue(response, PecaDTO.class);

        mockMvc.perform(patch("/api/pecas/" + createdPeca.getId() + "/estoque?quantidade=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPeca.getId()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(30));
    }

    @Test
    void testDeletarPeca() throws Exception {
        String response = mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PecaDTO createdPeca = objectMapper.readValue(response, PecaDTO.class);

        mockMvc.perform(delete("/api/pecas/" + createdPeca.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pecas/" + createdPeca.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidacaoCriarPecaSemNome() throws Exception {
        PecaDTO invalidDTO = PecaDTO.builder()
                .descricao("Peça de teste")
                .valor(new BigDecimal("45.00"))
                .quantidadeEstoque(20)
                .build();

        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarPecaValorZero() throws Exception {
        PecaDTO invalidDTO = PecaDTO.builder()
                .nome("Peça de Teste")
                .descricao("Peça de teste")
                .valor(new BigDecimal("0.00"))
                .quantidadeEstoque(20)
                .build();

        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarPecaEstoqueNegativo() throws Exception {
        PecaDTO invalidDTO = PecaDTO.builder()
                .nome("Peça de Teste")
                .descricao("Peça de teste")
                .valor(new BigDecimal("45.00"))
                .quantidadeEstoque(-5)
                .build();

        mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
