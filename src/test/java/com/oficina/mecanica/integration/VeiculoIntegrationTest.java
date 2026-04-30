package com.oficina.mecanica.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import com.oficina.mecanica.application.dto.ClienteDTO;
import com.oficina.mecanica.application.dto.VeiculoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VeiculoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;
    private VeiculoDTO veiculoDTO;

    @BeforeEach
    void setUp() throws Exception {
        clienteDTO = ClienteDTO.builder()
                .cpfCnpj("12345678901")
                .nome("João Silva")
                .telefone("11999999999")
                .email("joao@email.com")
                .build();

        String clienteResponse = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDTO createdCliente = objectMapper.readValue(clienteResponse, ClienteDTO.class);

        veiculoDTO = VeiculoDTO.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .clienteId(createdCliente.getId())
                .build();
    }

    @Test
    void testCriarVeiculo() throws Exception {
        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"))
                .andExpect(jsonPath("$.ano").value(2020))
                .andExpect(jsonPath("$.clienteId").exists());
    }

    @Test
    void testBuscarVeiculoPorId() throws Exception {
        String response = mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        VeiculoDTO createdVeiculo = objectMapper.readValue(response, VeiculoDTO.class);

        mockMvc.perform(get("/api/veiculos/" + createdVeiculo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdVeiculo.getId()))
                .andExpect(jsonPath("$.placa").value("ABC1234"));
    }

    @Test
    void testBuscarVeiculoPorPlaca() throws Exception {
        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/veiculos/placa/ABC1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    void testListarTodosVeiculos() throws Exception {
        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andExpect(status().isCreated());

        VeiculoDTO veiculo2 = VeiculoDTO.builder()
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2021)
                .clienteId(veiculoDTO.getClienteId())
                .build();

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testListarVeiculosPorCliente() throws Exception {
        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andExpect(status().isCreated());

        VeiculoDTO veiculo2 = VeiculoDTO.builder()
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2021)
                .clienteId(veiculoDTO.getClienteId())
                .build();

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/veiculos/cliente/" + veiculoDTO.getClienteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testAtualizarVeiculo() throws Exception {
        String response = mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        VeiculoDTO createdVeiculo = objectMapper.readValue(response, VeiculoDTO.class);

        VeiculoDTO updatedDTO = VeiculoDTO.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla XEI")
                .ano(2021)
                .clienteId(createdVeiculo.getClienteId())
                .build();

        mockMvc.perform(put("/api/veiculos/" + createdVeiculo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdVeiculo.getId()))
                .andExpect(jsonPath("$.modelo").value("Corolla XEI"))
                .andExpect(jsonPath("$.ano").value(2021));
    }

    @Test
    void testDeletarVeiculo() throws Exception {
        String response = mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        VeiculoDTO createdVeiculo = objectMapper.readValue(response, VeiculoDTO.class);

        mockMvc.perform(delete("/api/veiculos/" + createdVeiculo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/veiculos/" + createdVeiculo.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidacaoCriarVeiculoSemPlaca() throws Exception {
        VeiculoDTO invalidDTO = VeiculoDTO.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .clienteId(veiculoDTO.getClienteId())
                .build();

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarVeiculoSemMarca() throws Exception {
        VeiculoDTO invalidDTO = VeiculoDTO.builder()
                .placa("ABC1234")
                .modelo("Corolla")
                .ano(2020)
                .clienteId(veiculoDTO.getClienteId())
                .build();

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
