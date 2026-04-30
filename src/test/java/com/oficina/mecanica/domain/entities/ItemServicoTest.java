package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemServicoTest {

    @Test
    void deveDefinirValorUnitarioDoServicoNoOnPersistOrUpdate() {
        Servico servico = Servico.builder()
            .nome("Troca de Óleo")
            .descricao("Troca de óleo motor 5W30")
            .valor(new BigDecimal("150.00"))
            .tempoEstimadoMinutos(60)
            .build();

        ItemServico itemServico = ItemServico.builder()
            .servico(servico)
            .quantidade(1)
            .build();

        itemServico.onPersistOrUpdate();

        assertEquals(new BigDecimal("150.00"), itemServico.getValorUnitario());
    }

    @Test
    void deveCriarItemServicoComBuilder() {
        Servico servico = Servico.builder()
            .nome("Troca de Óleo")
            .descricao("Troca de óleo motor 5W30")
            .valor(new BigDecimal("150.00"))
            .tempoEstimadoMinutos(60)
            .build();

        ItemServico itemServico = ItemServico.builder()
            .id(1L)
            .servico(servico)
            .quantidade(1)
            .valorUnitario(new BigDecimal("150.00"))
            .build();

        assertEquals(1L, itemServico.getId());
        assertEquals(servico, itemServico.getServico());
        assertEquals(1, itemServico.getQuantidade());
        assertEquals(new BigDecimal("150.00"), itemServico.getValorUnitario());
    }

    @Test
    void deveCriarItemServicoComAllArgsConstructor() {
        Servico servico = Servico.builder()
            .nome("Troca de Óleo")
            .descricao("Troca de óleo motor 5W30")
            .valor(new BigDecimal("150.00"))
            .tempoEstimadoMinutos(60)
            .build();

        ItemServico itemServico = new ItemServico(1L, null, servico, 1, new BigDecimal("150.00"));

        assertEquals(1L, itemServico.getId());
        assertEquals(servico, itemServico.getServico());
        assertEquals(1, itemServico.getQuantidade());
        assertEquals(new BigDecimal("150.00"), itemServico.getValorUnitario());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        ItemServico itemServico = new ItemServico();

        assertNotNull(itemServico);
    }

    @Test
    void deveSetarEObterQuantidade() {
        ItemServico itemServico = new ItemServico();
        itemServico.setQuantidade(1);

        assertEquals(1, itemServico.getQuantidade());
    }

    @Test
    void deveSetarEObterValorUnitario() {
        ItemServico itemServico = new ItemServico();
        BigDecimal valor = new BigDecimal("150.00");
        itemServico.setValorUnitario(valor);

        assertEquals(valor, itemServico.getValorUnitario());
    }

    @Test
    void deveNaoDefinirValorUnitarioQuandoServicoNulo() {
        ItemServico itemServico = ItemServico.builder()
            .quantidade(1)
            .build();

        itemServico.onPersistOrUpdate();

        assertNull(itemServico.getValorUnitario());
    }
}
