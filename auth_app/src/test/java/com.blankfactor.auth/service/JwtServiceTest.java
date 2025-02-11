package com.blankfactor.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.userdetails.UserDetails;


import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey secretKey;

    private long jwtExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64SecretKey = Encoders.BASE64.encode(secretKey.getEncoded());
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", base64SecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
    }

    @Test
    void testGenerateAndExtractUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token, "Token should not be null");

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser@example.com", extractedUsername, "Extracted username should match the one from UserDetails");
    }

    @Test
    void testIsTokenValidSuccess() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid, "Token should be valid for the same user");
    }

    @Test
    void testIsTokenValidUserMismatch() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("otheruser@example.com");

        assertThrows(JwtException.class, () -> jwtService.isTokenValid(token, otherUser),
                "Token should not be valid for a different user");
    }

    @Test
    void testTokenExpiration() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails),
                "Expired token should throw ExpiredJwtException");
    }
}