package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DomainEntitiesCoverageTest {

    @Test
    void deveValidarEFormatarCpfCnpjDoCliente() {
        Cliente cliente = new Cliente();

        cliente.setCpfCnpj("529.982.247-25");

        assertEquals("52998224725", cliente.getCpfCnpj());
        assertEquals("529.982.247-25", cliente.getCpfCnpjFormatado());
    }

    @Test
    void deveLancarExcecaoParaCpfInvalido() {
        Cliente cliente = new Cliente();

        assertThrows(IllegalArgumentException.class, () -> cliente.setCpfCnpj("12345678901"));
    }

    @Test
    void deveValidarEFormatarPlacaDoVeiculo() {
        Veiculo veiculo = new Veiculo();

        veiculo.setPlaca("abc1d23");

        assertEquals("ABC1D23", veiculo.getPlaca());
        assertEquals("ABC-1D-23", veiculo.getPlacaFormatada());
    }

    @Test
    void deveInicializarDatasDeCadastroEAjustarAtualizacao() {
        Servico servico = new Servico();

        servico.onCreate();
        assertNotNull(servico.getDataCadastro());
        assertNotNull(servico.getDataAtualizacao());

        servico.onUpdate();
        assertNotNull(servico.getDataAtualizacao());
    }

    @Test
    void deveDefinirValorUnitarioNoItemServicoComBaseNoServico() {
        Servico servico = Servico.builder().valor(new BigDecimal("150.00")).build();
        ItemServico itemServico = ItemServico.builder().servico(servico).quantidade(2).build();

        itemServico.onPersistOrUpdate();

        assertEquals(new BigDecimal("150.00"), itemServico.getValorUnitario());
    }

    @Test
    void deveDefinirValorUnitarioNoItemPecaComBaseNaPeca() {
        Peca peca = Peca.builder().valor(new BigDecimal("45.00")).quantidadeEstoque(10).build();
        ItemPeca itemPeca = ItemPeca.builder().peca(peca).quantidade(1).build();

        itemPeca.onPersistOrUpdate();

        assertEquals(new BigDecimal("45.00"), itemPeca.getValorUnitario());
    }
}
