package com.oficina.mecanica.domain.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
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
}
