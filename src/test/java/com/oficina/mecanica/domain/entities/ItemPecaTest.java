package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemPecaTest {

    @Test
    void deveDefinirValorUnitarioDaPecaNoOnPersistOrUpdate() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(10)
            .build();

        ItemPeca itemPeca = ItemPeca.builder()
            .peca(peca)
            .quantidade(2)
            .build();

        itemPeca.onPersistOrUpdate();

        assertEquals(new BigDecimal("50.00"), itemPeca.getValorUnitario());
    }

    @Test
    void deveCriarItemPecaComBuilder() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(10)
            .build();

        ItemPeca itemPeca = ItemPeca.builder()
            .id(1L)
            .peca(peca)
            .quantidade(2)
            .valorUnitario(new BigDecimal("50.00"))
            .build();

        assertEquals(1L, itemPeca.getId());
        assertEquals(peca, itemPeca.getPeca());
        assertEquals(2, itemPeca.getQuantidade());
        assertEquals(new BigDecimal("50.00"), itemPeca.getValorUnitario());
    }

    @Test
    void deveCriarItemPecaComAllArgsConstructor() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(10)
            .build();

        ItemPeca itemPeca = new ItemPeca(1L, null, peca, 2, new BigDecimal("50.00"));

        assertEquals(1L, itemPeca.getId());
        assertEquals(peca, itemPeca.getPeca());
        assertEquals(2, itemPeca.getQuantidade());
        assertEquals(new BigDecimal("50.00"), itemPeca.getValorUnitario());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        ItemPeca itemPeca = new ItemPeca();

        assertNotNull(itemPeca);
    }

    @Test
    void deveSetarEObterQuantidade() {
        ItemPeca itemPeca = new ItemPeca();
        itemPeca.setQuantidade(2);

        assertEquals(2, itemPeca.getQuantidade());
    }

    @Test
    void deveSetarEObterValorUnitario() {
        ItemPeca itemPeca = new ItemPeca();
        BigDecimal valor = new BigDecimal("50.00");
        itemPeca.setValorUnitario(valor);

        assertEquals(valor, itemPeca.getValorUnitario());
    }

    @Test
    void deveNaoDefinirValorUnitarioQuandoPecaNula() {
        ItemPeca itemPeca = ItemPeca.builder()
            .quantidade(2)
            .build();

        itemPeca.onPersistOrUpdate();

        assertNull(itemPeca.getValorUnitario());
    }
}
