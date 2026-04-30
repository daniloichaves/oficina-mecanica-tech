package com.oficina.mecanica.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpfCnpjTest {
    
    @Test
    void deveCriarCpfValido() {
        CpfCnpj cpf = new CpfCnpj("529.982.247-25");
        assertEquals("52998224725", cpf.getValor());
        assertEquals("529.982.247-25", cpf.formatado());
    }
    
    @Test
    void deveCriarCnpjValido() {
        CpfCnpj cnpj = new CpfCnpj("12.345.678/0001-95");
        assertEquals("12345678000195", cnpj.getValor());
        assertEquals("12.345.678/0001-95", cnpj.formatado());
    }
    
    @Test
    void deveLancarExcecaoParaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("111.111.111-11"));
    }
    
    @Test
    void deveLancarExcecaoParaCnpjInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("00.000.000/0000-00"));
    }
    
    @Test
    void deveLancarExcecaoParaNulo() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj(null));
    }
    
    @Test
    void deveLancarExcecaoParaVazio() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj(""));
    }
}
