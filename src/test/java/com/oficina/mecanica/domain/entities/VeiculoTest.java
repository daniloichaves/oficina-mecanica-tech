package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    @Test
    void deveInicializarDatasNoOnCreate() {
        Veiculo veiculo = new Veiculo();

        veiculo.onCreate();

        assertNotNull(veiculo.getDataCadastro());
        assertNotNull(veiculo.getDataAtualizacao());
    }

    @Test
    void deveAtualizarDataNoOnUpdate() {
        Veiculo veiculo = new Veiculo();

        veiculo.onUpdate();

        assertNotNull(veiculo.getDataAtualizacao());
    }

    @Test
    void deveCriarVeiculoComBuilder() {
        Veiculo veiculo = Veiculo.builder()
            .id(1L)
            .placa("ABC1234")
            .marca("Toyota")
            .modelo("Corolla")
            .ano(2020)
            .build();

        assertEquals(1L, veiculo.getId());
        assertEquals("ABC1234", veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2020, veiculo.getAno());
    }

    @Test
    void deveCriarVeiculoComAllArgsConstructor() {
        Veiculo veiculo = new Veiculo(1L, "ABC1234", "Toyota", "Corolla", 2020, null, null, null, new ArrayList<>());

        assertEquals(1L, veiculo.getId());
        assertEquals("ABC1234", veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2020, veiculo.getAno());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        Veiculo veiculo = new Veiculo();

        assertNotNull(veiculo);
    }

    @Test
    void deveSetarEObterMarca() {
        Veiculo veiculo = new Veiculo();
        veiculo.setMarca("Toyota");

        assertEquals("Toyota", veiculo.getMarca());
    }

    @Test
    void deveSetarEObterModelo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Corolla");

        assertEquals("Corolla", veiculo.getModelo());
    }

    @Test
    void deveSetarEObterAno() {
        Veiculo veiculo = new Veiculo();
        veiculo.setAno(2020);

        assertEquals(2020, veiculo.getAno());
    }

    @Test
    void deveSetarPlacaComMascara() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC-1234");

        assertEquals("ABC1234", veiculo.getPlaca());
    }

    @Test
    void deveFormatarPlaca() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        String formatada = veiculo.getPlacaFormatada();

        assertNotNull(formatada);
    }
}
