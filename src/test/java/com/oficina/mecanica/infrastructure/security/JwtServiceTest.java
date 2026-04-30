package com.oficina.mecanica.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET = "oficinaMecanicaSecretKeyForJWTTokenGeneration2024";
    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void extractExpiration_ShouldReturnExpirationDate() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        boolean isValid = jwtService.validateToken(token, username);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForWrongUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        boolean isValid = jwtService.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    void extractUsername_ShouldExtractUsernameFromToken() {
        String username = "admin";
        String token = jwtService.generateToken(username);

        String extracted = jwtService.extractUsername(token);

        assertEquals(username, extracted);
    }
}
