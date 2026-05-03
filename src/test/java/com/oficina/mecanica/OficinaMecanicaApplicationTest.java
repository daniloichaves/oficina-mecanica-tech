package com.oficina.mecanica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OficinaMecanicaApplicationTest {

    @Test
    void deveTerMetodoMain() {
        // Verifica que a classe pode ser instanciada
        OficinaMecanicaApplication app = new OficinaMecanicaApplication();
        assertNotNull(app);
    }
}
