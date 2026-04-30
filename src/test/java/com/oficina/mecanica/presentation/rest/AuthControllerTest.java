package com.oficina.mecanica.presentation.rest;

import com.oficina.mecanica.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_ComCredenciaisValidas_DeveRetornarToken() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "password");
        when(jwtService.generateToken(anyString())).thenReturn("jwt.token.here");

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt.token.here", response.getBody().get("token"));
        assertEquals("testuser", response.getBody().get("username"));
        verify(jwtService).generateToken("testuser");
    }

    @Test
    void login_ComUsernameNulo_DeveRetornarBadRequest() {
        AuthController.LoginRequest request = new AuthController.LoginRequest(null, "password");

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(400, response.getStatusCode().value());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_ComUsernameVazio_DeveRetornarBadRequest() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("", "password");

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(400, response.getStatusCode().value());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_ComPasswordNulo_DeveRetornarBadRequest() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", null);

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(400, response.getStatusCode().value());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_ComPasswordVazio_DeveRetornarBadRequest() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "");

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(400, response.getStatusCode().value());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginRequest_DeveSetarEObterUsername() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "password");

        assertEquals("testuser", request.getUsername());
    }

    @Test
    void loginRequest_DeveSetarEObterPassword() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "password");

        assertEquals("password", request.getPassword());
    }
}
