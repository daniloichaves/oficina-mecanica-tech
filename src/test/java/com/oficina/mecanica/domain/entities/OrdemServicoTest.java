package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {
    
    @Test
    void deveIniciarComStatusRecebida() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();
        
        assertEquals(StatusOrdemServico.RECEBIDA, os.getStatus());
    }
    
    @Test
    void deveIniciarDiagnostico() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();
        
        os.iniciarDiagnostico();
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, os.getStatus());
    }
    
    @Test
    void naoDeveIniciarDiagnosticoSeNaoRecebida() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .build();
        
        assertThrows(IllegalStateException.class, os::iniciarDiagnostico);
    }
    
    @Test
    void deveConcluirDiagnostico() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .build();
        
        os.concluirDiagnostico();
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, os.getStatus());
    }
    
    @Test
    void deveAprovarOrcamento() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.AGUARDANDO_APROVACAO)
            .orcamentoAprovado(false)
            .build();
        
        os.aprovarOrcamento();
        assertEquals(StatusOrdemServico.EM_EXECUCAO, os.getStatus());
        assertTrue(os.getOrcamentoAprovado());
    }
    
    @Test
    void deveFinalizar() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_EXECUCAO)
            .build();
        
        os.finalizar();
        assertEquals(StatusOrdemServico.FINALIZADA, os.getStatus());
    }
    
    @Test
    void deveEntregar() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.FINALIZADA)
            .build();
        
        os.entregar();
        assertEquals(StatusOrdemServico.ENTREGUE, os.getStatus());
        assertNotNull(os.getDataEntrega());
    }

    @Test
    void naoDeveConcluirDiagnosticoSeStatusInvalido() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();

        assertThrows(IllegalStateException.class, os::concluirDiagnostico);
    }

    @Test
    void naoDeveAprovarOrcamentoSeStatusInvalido() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .build();

        assertThrows(IllegalStateException.class, os::aprovarOrcamento);
    }

    @Test
    void naoDeveFinalizarSeStatusInvalido() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();

        assertThrows(IllegalStateException.class, os::finalizar);
    }

    @Test
    void naoDeveEntregarSeStatusInvalido() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_EXECUCAO)
            .build();

        assertThrows(IllegalStateException.class, os::entregar);
    }

    @Test
    void deveCalcularOrcamentoComServicosEPecas() {
        Servico servico = Servico.builder().valor(new BigDecimal("100.00")).build();
        Peca peca = Peca.builder().valor(new BigDecimal("50.00")).quantidadeEstoque(10).build();

        ItemServico itemServico = ItemServico.builder()
            .servico(servico)
            .quantidade(2)
            .build();

        ItemPeca itemPeca = ItemPeca.builder()
            .peca(peca)
            .quantidade(1)
            .build();

        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .itensServico(List.of(itemServico))
            .itensPeca(List.of(itemPeca))
            .build();

        os.calcularOrcamento();

        assertEquals(new BigDecimal("250.00"), os.getValorTotal());
    }

    @Test
    void deveCalcularOrcamentoApenasComServicos() {
        Servico servico = Servico.builder().valor(new BigDecimal("100.00")).build();

        ItemServico itemServico = ItemServico.builder()
            .servico(servico)
            .quantidade(3)
            .build();

        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .itensServico(List.of(itemServico))
            .itensPeca(List.of())
            .build();

        os.calcularOrcamento();

        assertEquals(new BigDecimal("300.00"), os.getValorTotal());
    }

    @Test
    void deveCalcularOrcamentoApenasComPecas() {
        Peca peca = Peca.builder().valor(new BigDecimal("50.00")).quantidadeEstoque(10).build();

        ItemPeca itemPeca = ItemPeca.builder()
            .peca(peca)
            .quantidade(2)
            .build();

        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .itensServico(List.of())
            .itensPeca(List.of(itemPeca))
            .build();

        os.calcularOrcamento();

        assertEquals(new BigDecimal("100.00"), os.getValorTotal());
    }

    @Test
    void deveCalcularOrcamentoComItensVazios() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.EM_DIAGNOSTICO)
            .itensServico(List.of())
            .itensPeca(List.of())
            .build();

        os.calcularOrcamento();

        assertEquals(BigDecimal.ZERO, os.getValorTotal());
    }

    @Test
    void deveInicializarCamposNoOnCreate() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .build();

        os.onCreate();

        assertNotNull(os.getDataCriacao());
        assertNotNull(os.getDataAtualizacao());
        assertEquals(StatusOrdemServico.RECEBIDA, os.getStatus());
        assertFalse(os.getOrcamentoAprovado());
    }

    @Test
    void deveAtualizarDataNoOnUpdate() {
        OrdemServico os = OrdemServico.builder()
            .cliente(new Cliente())
            .veiculo(new Veiculo())
            .status(StatusOrdemServico.RECEBIDA)
            .build();

        os.onUpdate();

        assertNotNull(os.getDataAtualizacao());
    }

    @Test
    void deveCriarOrdemServicoComBuilder() {
        Cliente cliente = Cliente.builder().id(1L).nome("João").build();
        Veiculo veiculo = Veiculo.builder().id(1L).placa("ABC1234").build();

        OrdemServico os = OrdemServico.builder()
            .id(1L)
            .cliente(cliente)
            .veiculo(veiculo)
            .status(StatusOrdemServico.RECEBIDA)
            .build();

        assertEquals(1L, os.getId());
        assertEquals(cliente, os.getCliente());
        assertEquals(veiculo, os.getVeiculo());
        assertEquals(StatusOrdemServico.RECEBIDA, os.getStatus());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        OrdemServico os = new OrdemServico();

        assertNotNull(os);
    }

    @Test
    void deveSetarEObterCliente() {
        OrdemServico os = new OrdemServico();
        Cliente cliente = Cliente.builder().id(1L).build();
        os.setCliente(cliente);

        assertEquals(cliente, os.getCliente());
    }

    @Test
    void deveSetarEObterVeiculo() {
        OrdemServico os = new OrdemServico();
        Veiculo veiculo = Veiculo.builder().id(1L).build();
        os.setVeiculo(veiculo);

        assertEquals(veiculo, os.getVeiculo());
    }

    @Test
    void deveSetarEObterStatus() {
        OrdemServico os = new OrdemServico();
        os.setStatus(StatusOrdemServico.EM_EXECUCAO);

        assertEquals(StatusOrdemServico.EM_EXECUCAO, os.getStatus());
    }

    @Test
    void deveSetarEObterValorTotal() {
        OrdemServico os = new OrdemServico();
        BigDecimal valor = new BigDecimal("500.00");
        os.setValorTotal(valor);

        assertEquals(valor, os.getValorTotal());
    }

    @Test
    void deveSetarEObterOrcamentoAprovado() {
        OrdemServico os = new OrdemServico();
        os.setOrcamentoAprovado(true);

        assertTrue(os.getOrcamentoAprovado());
    }

    @Test
    void deveSetarEObterDataCriacao() {
        OrdemServico os = new OrdemServico();
        LocalDateTime data = LocalDateTime.now();
        os.setDataCriacao(data);

        assertEquals(data, os.getDataCriacao());
    }

    @Test
    void deveSetarEObterDataAtualizacao() {
        OrdemServico os = new OrdemServico();
        LocalDateTime data = LocalDateTime.now();
        os.setDataAtualizacao(data);

        assertEquals(data, os.getDataAtualizacao());
    }

    @Test
    void deveSetarEObterDataEntrega() {
        OrdemServico os = new OrdemServico();
        LocalDateTime data = LocalDateTime.now();
        os.setDataEntrega(data);

        assertEquals(data, os.getDataEntrega());
    }

    @Test
    void deveSetarEObterItensServico() {
        OrdemServico os = new OrdemServico();
        List<ItemServico> itens = List.of(ItemServico.builder().build());
        os.setItensServico(itens);

        assertEquals(itens, os.getItensServico());
    }

    @Test
    void deveSetarEObterItensPeca() {
        OrdemServico os = new OrdemServico();
        List<ItemPeca> itens = List.of(ItemPeca.builder().build());
        os.setItensPeca(itens);

        assertEquals(itens, os.getItensPeca());
    }
}
