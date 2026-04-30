package com.oficina.mecanica.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI oficinaMecanicaOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Oficina Mecânica API")
                .description("API para gestão de ordens de serviço, clientes, veículos, serviços e peças")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Tech Challenge")
                    .email("techchallenge@oficina.com")));
    }
}
