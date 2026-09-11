package com.oficina.mecanica.integration;

import com.oficina.mecanica.infrastructure.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(IntegrationTestBase.AuthenticatedMockMvcConfiguration.class)
public abstract class IntegrationTestBase {

    @TestConfiguration
    static class AuthenticatedMockMvcConfiguration {

        @Bean
        MockMvcBuilderCustomizer authenticatedRequests(JwtService jwtService) {
            return builder -> builder.defaultRequest(get("/")
                .header("Authorization", "Bearer " + jwtService.generateToken("integration-test")));
        }
    }
}
