package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    @Test
    void deveInicializarDatasNoOnCreate() {
        Servico servico = new Servico();

        servico.onCreate();

        assertNotNull(servico.getDataCadastro());
        assertNotNull(servico.getDataAtualizacao());
    }

    @Test
    void deveAtualizarDataNoOnUpdate() {
        Servico servico = new Servico();

        servico.onUpdate();

        assertNotNull(servico.getDataAtualizacao());
    }

    @Test
    void deveCriarServicoComBuilder() {
        Servico servico = Servico.builder()
            .id(1L)
            .nome("Troca de Óleo")
            .descricao("Troca de óleo motor 5W30")
            .valor(new BigDecimal("150.00"))
            .tempoEstimadoMinutos(60)
            .build();

        assertEquals(1L, servico.getId());
        assertEquals("Troca de Óleo", servico.getNome());
        assertEquals("Troca de óleo motor 5W30", servico.getDescricao());
        assertEquals(new BigDecimal("150.00"), servico.getValor());
        assertEquals(60, servico.getTempoEstimadoMinutos());
    }

    @Test
    void deveCriarServicoComAllArgsConstructor() {
        Servico servico = new Servico(1L, "Troca de Óleo", "Troca de óleo motor 5W30", new BigDecimal("150.00"), 60, null, null);

        assertEquals(1L, servico.getId());
        assertEquals("Troca de Óleo", servico.getNome());
        assertEquals("Troca de óleo motor 5W30", servico.getDescricao());
        assertEquals(new BigDecimal("150.00"), servico.getValor());
        assertEquals(60, servico.getTempoEstimadoMinutos());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        Servico servico = new Servico();

        assertNotNull(servico);
    }

    @Test
    void deveSetarEObterNome() {
        Servico servico = new Servico();
        servico.setNome("Troca de Óleo");

        assertEquals("Troca de Óleo", servico.getNome());
    }

    @Test
    void deveSetarEObterValor() {
        Servico servico = new Servico();
        BigDecimal valor = new BigDecimal("150.00");
        servico.setValor(valor);

        assertEquals(valor, servico.getValor());
    }

    @Test
    void deveSetarEObterTempoEstimadoMinutos() {
        Servico servico = new Servico();
        servico.setTempoEstimadoMinutos(60);

        assertEquals(60, servico.getTempoEstimadoMinutos());
    }

    @Test
    void deveSetarEObterDataCadastro() {
        Servico servico = new Servico();
        LocalDateTime data = LocalDateTime.now();
        servico.setDataCadastro(data);

        assertEquals(data, servico.getDataCadastro());
    }

    @Test
    void deveSetarEObterDataAtualizacao() {
        Servico servico = new Servico();
        LocalDateTime data = LocalDateTime.now();
        servico.setDataAtualizacao(data);

        assertEquals(data, servico.getDataAtualizacao());
    }
}
