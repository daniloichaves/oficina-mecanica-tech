package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveInicializarDatasNoOnCreate() {
        Cliente cliente = new Cliente();

        cliente.onCreate();

        assertNotNull(cliente.getDataCadastro());
        assertNotNull(cliente.getDataAtualizacao());
    }

    @Test
    void deveAtualizarDataNoOnUpdate() {
        Cliente cliente = new Cliente();

        cliente.onUpdate();

        assertNotNull(cliente.getDataAtualizacao());
    }

    @Test
    void deveCriarClienteComBuilder() {
        Cliente cliente = Cliente.builder()
            .id(1L)
            .cpfCnpj("52998224725")
            .nome("João Silva")
            .telefone("11999999999")
            .email("joao@email.com")
            .endereco("Rua Teste, 123")
            .build();

        assertEquals(1L, cliente.getId());
        assertEquals("52998224725", cliente.getCpfCnpj());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("11999999999", cliente.getTelefone());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("Rua Teste, 123", cliente.getEndereco());
    }

    @Test
    void deveCriarClienteComAllArgsConstructor() {
        Cliente cliente = new Cliente(1L, "52998224725", "João Silva", "11999999999", "joao@email.com", "Rua Teste, 123", null, null, new ArrayList<>());

        assertEquals(1L, cliente.getId());
        assertEquals("52998224725", cliente.getCpfCnpj());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("11999999999", cliente.getTelefone());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("Rua Teste, 123", cliente.getEndereco());
    }

    @Test
    void deveUsarNoArgsConstructor() {
        Cliente cliente = new Cliente();

        assertNotNull(cliente);
    }

    @Test
    void deveSetarEObterNome() {
        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");

        assertEquals("João Silva", cliente.getNome());
    }

    @Test
    void deveSetarEObterTelefone() {
        Cliente cliente = new Cliente();
        cliente.setTelefone("11999999999");

        assertEquals("11999999999", cliente.getTelefone());
    }

    @Test
    void deveSetarEObterEmail() {
        Cliente cliente = new Cliente();
        cliente.setEmail("joao@email.com");

        assertEquals("joao@email.com", cliente.getEmail());
    }

    @Test
    void deveSetarEObterEndereco() {
        Cliente cliente = new Cliente();
        cliente.setEndereco("Rua Teste, 123");

        assertEquals("Rua Teste, 123", cliente.getEndereco());
    }

    @Test
    void deveSetarCpfCnpjComMascara() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("529.982.247-25");

        assertEquals("52998224725", cliente.getCpfCnpj());
    }

    @Test
    void deveFormatarCpfCnpj() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("52998224725");

        String formatado = cliente.getCpfCnpjFormatado();

        assertNotNull(formatado);
        assertEquals("529.982.247-25", formatado);
    }
}
