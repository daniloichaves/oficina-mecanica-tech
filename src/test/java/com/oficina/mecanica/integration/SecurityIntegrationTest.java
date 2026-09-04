package com.oficina.mecanica.integration;

import com.oficina.mecanica.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void deveRecusarRotaDeNegocioSemToken() throws Exception {
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAceitarRotaDeNegocioComTokenValido() throws Exception {
        String token = jwtService.generateToken("52998224725");

        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void deveRecusarTokenMalformado() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer token-invalido"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveManterHealthcheckPublico() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
            .andExpect(status().isOk());
    }
}
