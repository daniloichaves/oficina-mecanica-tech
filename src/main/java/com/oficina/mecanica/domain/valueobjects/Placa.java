package com.oficina.mecanica.domain.valueobjects;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class Placa {
    private final String valor;
    
    private static final Pattern PLACA_MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");
    private static final Pattern PLACA_ANTIGA = Pattern.compile("^[A-Z]{3}[0-9]{4}$");
    
    public Placa(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");
        }
        String valorLimpo = valor.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!isValidPlaca(valorLimpo)) {
            throw new IllegalArgumentException("Placa inválida: " + valor);
        }
        this.valor = valorLimpo;
    }
    
    private boolean isValidPlaca(String placa) {
        return PLACA_MERCOSUL.matcher(placa).matches() || PLACA_ANTIGA.matcher(placa).matches();
    }
    
    public String formatada() {
        if (valor.length() == 7) {
            return valor.replaceAll("([A-Z]{3})([0-9])([A-Z0-9])([0-9]{2})", "$1-$2$3-$4");
        } else {
            return valor.replaceAll("([A-Z]{3})([0-9]{4})", "$1-$2");
        }
    }
    
    @Override
    public String toString() {
        return valor;
    }
}
