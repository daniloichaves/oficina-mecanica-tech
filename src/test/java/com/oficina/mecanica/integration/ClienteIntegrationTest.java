package com.oficina.mecanica.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import com.oficina.mecanica.application.dto.ClienteDTO;
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
public class ClienteIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteDTO = ClienteDTO.builder()
                .cpfCnpj("52998224725")
                .nome("João Silva")
                .telefone("11999999999")
                .email("joao@email.com")
                .endereco("Rua Teste, 123")
                .build();
    }

    @Test
    void testCriarCliente() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.endereco").value("Rua Teste, 123"));
    }

    @Test
    void testBuscarClientePorId() throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDTO createdCliente = objectMapper.readValue(response, ClienteDTO.class);

        mockMvc.perform(get("/api/clientes/" + createdCliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCliente.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void testBuscarClientePorCpfCnpj() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clientes/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void testListarTodosClientes() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());

        ClienteDTO cliente2 = ClienteDTO.builder()
                .cpfCnpj("98765432100")
                .nome("Maria Santos")
                .telefone("11888888888")
                .email("maria@email.com")
                .build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testAtualizarCliente() throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDTO createdCliente = objectMapper.readValue(response, ClienteDTO.class);

        ClienteDTO updatedDTO = ClienteDTO.builder()
                .cpfCnpj("12345678901")
                .nome("João Silva Atualizado")
                .telefone("11999999999")
                .email("joao.novo@email.com")
                .endereco("Rua Nova, 456")
                .build();

        mockMvc.perform(put("/api/clientes/" + createdCliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCliente.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.email").value("joao.novo@email.com"));
    }

    @Test
    void testDeletarCliente() throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDTO createdCliente = objectMapper.readValue(response, ClienteDTO.class);

        mockMvc.perform(delete("/api/clientes/" + createdCliente.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clientes/" + createdCliente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidacaoCriarClienteSemNome() throws Exception {
        ClienteDTO invalidDTO = ClienteDTO.builder()
                .cpfCnpj("12345678901")
                .telefone("11999999999")
                .build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarClienteSemCpf() throws Exception {
        ClienteDTO invalidDTO = ClienteDTO.builder()
                .nome("João Silva")
                .telefone("11999999999")
                .build();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
