package com.oficina.mecanica.domain.valueobjects;

import lombok.Getter;

@Getter
public class CpfCnpj {
    private final String valor;
    
    public CpfCnpj(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF/CNPJ não pode ser nulo ou vazio");
        }
        String valorLimpo = valor.replaceAll("[^0-9]", "");
        if (!isValidCpfCnpj(valorLimpo)) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
        this.valor = valorLimpo;
    }
    
    private boolean isValidCpfCnpj(String valor) {
        if (valor.length() == 11) {
            return isValidCpf(valor);
        } else if (valor.length() == 14) {
            return isValidCnpj(valor);
        }
        return false;
    }
    
    private boolean isValidCpf(String cpf) {
        if (cpf.equals("00000000000") || cpf.equals("11111111111") || 
            cpf.equals("22222222222") || cpf.equals("33333333333") || 
            cpf.equals("44444444444") || cpf.equals("55555555555") || 
            cpf.equals("66666666666") || cpf.equals("77777777777") || 
            cpf.equals("88888888888") || cpf.equals("99999999999")) {
            return false;
        }
        
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) digito1 = 0;
        
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) digito2 = 0;
        
        return digito1 == Character.getNumericValue(cpf.charAt(9)) && 
               digito2 == Character.getNumericValue(cpf.charAt(10));
    }
    
    private boolean isValidCnpj(String cnpj) {
        if (cnpj.equals("00000000000000")) return false;
        
        int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) digito1 = 0;
        
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) digito2 = 0;
        
        return digito1 == Character.getNumericValue(cnpj.charAt(12)) && 
               digito2 == Character.getNumericValue(cnpj.charAt(13));
    }
    
    public String formatado() {
        if (valor.length() == 11) {
            return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else {
            return valor.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
    }
    
    @Override
    public String toString() {
        return valor;
    }
}
