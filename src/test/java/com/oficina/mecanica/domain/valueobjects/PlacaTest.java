package com.oficina.mecanica.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlacaTest {
    
    @Test
    void deveCriarPlacaAntigaValida() {
        Placa placa = new Placa("ABC-1234");
        assertEquals("ABC1234", placa.getValor());
        assertEquals("ABC-12-34", placa.formatada());
    }
    
    @Test
    void deveCriarPlacaMercosulValida() {
        Placa placa = new Placa("ABC1D23");
        assertEquals("ABC1D23", placa.getValor());
        assertEquals("ABC-1D-23", placa.formatada());
    }
    
    @Test
    void deveAceitarPlacaSemHifen() {
        Placa placa = new Placa("ABC1234");
        assertEquals("ABC1234", placa.getValor());
    }
    
    @Test
    void deveLancarExcecaoParaPlacaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Placa("AB1234"));
    }
    
    @Test
    void deveLancarExcecaoParaNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Placa(null));
    }
    
    @Test
    void deveLancarExcecaoParaVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Placa(""));
    }
}
