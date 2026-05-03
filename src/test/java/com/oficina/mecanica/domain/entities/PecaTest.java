package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PecaTest {
    
    @Test
    void deveDiminuirEstoqueSuficiente() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(10)
            .build();
        
        peca.diminuirEstoque(5);
        assertEquals(5, peca.getQuantidadeEstoque());
    }
    
    @Test
    void deveLancarExcecaoEstoqueInsuficiente() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(3)
            .build();
        
        assertThrows(IllegalArgumentException.class, () -> peca.diminuirEstoque(5));
    }
    
    @Test
    void deveAumentarEstoque() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(5)
            .build();
        
        peca.aumentarEstoque(3);
        assertEquals(8, peca.getQuantidadeEstoque());
    }

    @Test
    void deveInicializarDatasNoOnCreate() {
        Peca peca = new Peca();

        peca.onCreate();

        assertNotNull(peca.getDataCadastro());
        assertNotNull(peca.getDataAtualizacao());
    }

    @Test
    void deveAtualizarDataNoOnUpdate() {
        Peca peca = new Peca();
        LocalDateTime dataAntiga = LocalDateTime.now().minusDays(1);
        peca.setDataAtualizacao(dataAntiga);

        peca.onUpdate();

        assertNotNull(peca.getDataAtualizacao());
        assertTrue(peca.getDataAtualizacao().isAfter(dataAntiga) || peca.getDataAtualizacao().isEqual(dataAntiga));
    }

    @Test
    void deveDiminuirEstoqueZero() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(5)
            .build();

        peca.diminuirEstoque(0);
        assertEquals(5, peca.getQuantidadeEstoque());
    }

    @Test
    void deveAumentarEstoqueZero() {
        Peca peca = Peca.builder()
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(5)
            .build();

        peca.aumentarEstoque(0);
        assertEquals(5, peca.getQuantidadeEstoque());
    }

    @Test
    void deveCriarPecaComBuilder() {
        Peca peca = Peca.builder()
            .id(1L)
            .nome("Óleo Motor")
            .descricao("Óleo 5W30")
            .valor(new BigDecimal("50.00"))
            .quantidadeEstoque(10)
            .build();

        assertEquals(1L, peca.getId());
        assertEquals("Óleo Motor", peca.getNome());
        assertEquals("Óleo 5W30", peca.getDescricao());
        assertEquals(new BigDecimal("50.00"), peca.getValor());
        assertEquals(10, peca.getQuantidadeEstoque());
    }

    @Test
    void deveCriarPecaComAllArgsConstructor() {
        Peca peca = new Peca(1L, "Óleo Motor", "Óleo 5W30", new BigDecimal("50.00"), 10, null, null);

        assertEquals(1L, peca.getId());
        assertEquals("Óleo Motor", peca.getNome());
        assertEquals("Óleo 5W30", peca.getDescricao());
        assertEquals(new BigDecimal("50.00"), peca.getValor());
        assertEquals(10, peca.getQuantidadeEstoque());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        Peca peca = new Peca();

        assertNotNull(peca);
    }

    @Test
    void deveSetarEObterId() {
        Peca peca = new Peca();
        peca.setId(1L);

        assertEquals(1L, peca.getId());
    }

    @Test
    void deveSetarEObterNome() {
        Peca peca = new Peca();
        peca.setNome("Óleo Motor");

        assertEquals("Óleo Motor", peca.getNome());
    }

    @Test
    void deveSetarEObterDescricao() {
        Peca peca = new Peca();
        peca.setDescricao("Óleo 5W30");

        assertEquals("Óleo 5W30", peca.getDescricao());
    }

    @Test
    void deveSetarEObterValor() {
        Peca peca = new Peca();
        BigDecimal valor = new BigDecimal("50.00");
        peca.setValor(valor);

        assertEquals(valor, peca.getValor());
    }

    @Test
    void deveSetarEObterQuantidadeEstoque() {
        Peca peca = new Peca();
        peca.setQuantidadeEstoque(10);

        assertEquals(10, peca.getQuantidadeEstoque());
    }

    @Test
    void deveSetarEObterDataCadastro() {
        Peca peca = new Peca();
        LocalDateTime data = LocalDateTime.now();
        peca.setDataCadastro(data);

        assertEquals(data, peca.getDataCadastro());
    }

    @Test
    void deveSetarEObterDataAtualizacao() {
        Peca peca = new Peca();
        LocalDateTime data = LocalDateTime.now();
        peca.setDataAtualizacao(data);

        assertEquals(data, peca.getDataAtualizacao());
    }
}
