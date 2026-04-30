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
    void deveCriarCpfValidoSemMascara() {
        CpfCnpj cpf = new CpfCnpj("52998224725");
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
    void deveCriarCnpjValidoSemMascara() {
        CpfCnpj cnpj = new CpfCnpj("12345678000195");
        assertEquals("12345678000195", cnpj.getValor());
        assertEquals("12.345.678/0001-95", cnpj.formatado());
    }
    
    @Test
    void deveLancarExcecaoParaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("111.111.111-11"));
    }
    
    @Test
    void deveLancarExcecaoParaCpfInvalidoSemMascara() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("11111111111"));
    }
    
    @Test
    void deveLancarExcecaoParaCnpjInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("00.000.000/0000-00"));
    }
    
    @Test
    void deveLancarExcecaoParaCnpjInvalidoSemMascara() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("00000000000000"));
    }
    
    @Test
    void deveLancarExcecaoParaNulo() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj(null));
    }
    
    @Test
    void deveLancarExcecaoParaVazio() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj(""));
    }
    
    @Test
    void deveLancarExcecaoParaEspacosEmBranco() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("   "));
    }
    
    @Test
    void deveLancarExcecaoParaTamanhoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("123"));
    }
    
    @Test
    void deveLancarExcecaoParaCpfComLetras() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("529.982.247-AB"));
    }
    
    @Test
    void deveLancarExcecaoParaCnpjComLetras() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("12.345.678/0001-AB"));
    }
    
    @Test
    void deveLancarExcecaoParaCpfComTodosDigitosIguais() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("22222222222"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("33333333333"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("44444444444"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("55555555555"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("66666666666"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("77777777777"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("88888888888"));
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("99999999999"));
    }
    
    @Test
    void deveRetornarToString() {
        CpfCnpj cpf = new CpfCnpj("529.982.247-25");
        assertEquals("52998224725", cpf.toString());
    }
    
    @Test
    void deveFormatarCpf() {
        CpfCnpj cpf = new CpfCnpj("52998224725");
        assertEquals("529.982.247-25", cpf.formatado());
    }
    
    @Test
    void deveFormatarCnpj() {
        CpfCnpj cnpj = new CpfCnpj("12345678000195");
        assertEquals("12.345.678/0001-95", cnpj.formatado());
    }
}
