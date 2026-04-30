package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Autenticação e geração de tokens")
public class AuthController {
    
    private final JwtService jwtService;
    
    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login e geração de token JWT")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        // MVP: Autenticação simplificada - qualquer usuário com senha válida gera token
        // Em produção, implementar autenticação real com banco de dados
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
            request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(Map.of("token", token, "username", request.getUsername()));
    }
    
    @Data
    @AllArgsConstructor
    static class LoginRequest {
        private String username;
        private String password;
    }
}
