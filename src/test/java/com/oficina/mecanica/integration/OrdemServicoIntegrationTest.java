package com.oficina.mecanica.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import com.oficina.mecanica.application.dto.*;
import com.oficina.mecanica.domain.entities.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrdemServicoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long clienteId;
    private Long veiculoId;
    private Long servicoId;
    private Long pecaId;

    @BeforeEach
    void setUp() throws Exception {
        // Create Cliente
        ClienteDTO clienteDTO = ClienteDTO.builder()
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
        clienteId = createdCliente.getId();

        // Create Veiculo
        VeiculoDTO veiculoDTO = VeiculoDTO.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .clienteId(clienteId)
                .build();

        String veiculoResponse = mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        VeiculoDTO createdVeiculo = objectMapper.readValue(veiculoResponse, VeiculoDTO.class);
        veiculoId = createdVeiculo.getId();

        // Create Servico
        ServicoDTO servicoDTO = ServicoDTO.builder()
                .nome("Troca de Óleo")
                .descricao("Troca completa de óleo")
                .valor(new BigDecimal("150.00"))
                .tempoEstimadoMinutos(30)
                .build();

        String servicoResponse = mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ServicoDTO createdServico = objectMapper.readValue(servicoResponse, ServicoDTO.class);
        servicoId = createdServico.getId();

        // Create Peca
        PecaDTO pecaDTO = PecaDTO.builder()
                .nome("Filtro de Óleo")
                .descricao("Filtro de óleo")
                .valor(new BigDecimal("45.00"))
                .quantidadeEstoque(20)
                .build();

        String pecaResponse = mockMvc.perform(post("/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pecaDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PecaDTO createdPeca = objectMapper.readValue(pecaResponse, PecaDTO.class);
        pecaId = createdPeca.getId();
    }

    @Test
    void testCriarOrdemServico() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .itensPeca(List.of(
                        ItemPecaDTO.builder()
                                .pecaId(pecaId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.veiculoId").value(veiculoId))
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.RECEBIDA.name()))
                .andExpect(jsonPath("$.valorTotal").exists())
                .andExpect(jsonPath("$.itensServico", hasSize(1)))
                .andExpect(jsonPath("$.itensPeca", hasSize(1)));
    }

    @Test
    void testBuscarOrdemServicoPorId() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        String response = mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrdemServicoDTO createdOS = objectMapper.readValue(response, OrdemServicoDTO.class);

        mockMvc.perform(get("/api/ordens-servico/" + createdOS.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOS.getId()))
                .andExpect(jsonPath("$.clienteId").value(clienteId));
    }

    @Test
    void testListarTodasOrdensServico() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ordens-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testListarOrdensServicoPorCliente() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ordens-servico/cliente/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].clienteId").value(clienteId));
    }

    @Test
    void testListarOrdensServicoPorVeiculo() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ordens-servico/veiculo/" + veiculoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].veiculoId").value(veiculoId));
    }

    @Test
    void testListarOrdensServicoPorStatus() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ordens-servico/status/RECEBIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status").value(StatusOrdemServico.RECEBIDA.name()));
    }

    @Test
    void testFluxoCompletoCriacaoOS() throws Exception {
        // Complete flow: Create client, vehicle, service, part, and OS
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .itensPeca(List.of(
                        ItemPecaDTO.builder()
                                .pecaId(pecaId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        String response = mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrdemServicoDTO createdOS = objectMapper.readValue(response, OrdemServicoDTO.class);

        // Verify OS was created with correct data
        mockMvc.perform(get("/api/ordens-servico/" + createdOS.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOS.getId()))
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.veiculoId").value(veiculoId))
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.RECEBIDA.name()))
                .andExpect(jsonPath("$.valorTotal").value(195.00)) // 150 + 45
                .andExpect(jsonPath("$.itensServico[0].servicoId").value(servicoId))
                .andExpect(jsonPath("$.itensPeca[0].pecaId").value(pecaId));
    }

    @Test
    void testFluxoAprovacaoOrcamento() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        String response = mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrdemServicoDTO createdOS = objectMapper.readValue(response, OrdemServicoDTO.class);

        // Start diagnosis
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/iniciar-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.EM_DIAGNOSTICO.name()));

        // Complete diagnosis
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/concluir-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.AGUARDANDO_APROVACAO.name()));

        // Approve budget
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/aprovar-orcamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.EM_EXECUCAO.name()))
                .andExpect(jsonPath("$.orcamentoAprovado").value(true));
    }

    @Test
    void testFluxoMudancaStatusOS() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        String response = mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrdemServicoDTO createdOS = objectMapper.readValue(response, OrdemServicoDTO.class);

        // ABERTA -> EM_DIAGNOSTICO
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/iniciar-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.EM_DIAGNOSTICO.name()));

        // EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/concluir-diagnostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.AGUARDANDO_APROVACAO.name()));

        // AGUARDANDO_APROVACAO -> EM_EXECUCAO
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/aprovar-orcamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.EM_EXECUCAO.name()));

        // EM_EXECUCAO -> FINALIZADA (via finalizar endpoint)
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.FINALIZADA.name()));

        // FINALIZADA -> ENTREGUE
        mockMvc.perform(patch("/api/ordens-servico/" + createdOS.getId() + "/entregar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusOrdemServico.ENTREGUE.name()))
                .andExpect(jsonPath("$.dataEntrega").exists());
    }

    @Test
    void testValidacaoCriarOSSemCliente() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .veiculoId(veiculoId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarOSSemVeiculo() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .itensServico(List.of(
                        ItemServicoDTO.builder()
                                .servicoId(servicoId)
                                .quantidade(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoCriarOSSemServicos() throws Exception {
        CriarOrdemServicoDTO osDTO = CriarOrdemServicoDTO.builder()
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .itensServico(List.of())
                .build();

        mockMvc.perform(post("/api/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(osDTO)))
                .andExpect(status().isBadRequest());
    }
}
