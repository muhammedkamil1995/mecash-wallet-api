package com.mecash.wallet.security;

import io.jsonwebtoken.Claims;
import com.mecash.wallet.config.JwtProperties;
import com.mecash.wallet.model.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtProperties jwtProperties;
    private UserDetails userDetails;

    private static final String TEST_EMAIL = "test@example.com";
    private static final RoleType TEST_ROLE = RoleType.USER;
    private static final String SECRET_KEY = "EkRiKdVkGzUzIBC7pH6avBs1uOykwHpW";
    private static final long EXPIRATION_TIME = 3600000L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock JwtProperties
        jwtProperties = Mockito.mock(JwtProperties.class);
        Mockito.when(jwtProperties.getSecret()).thenReturn(SECRET_KEY);
        Mockito.when(jwtProperties.getExpirationTime()).thenReturn(EXPIRATION_TIME);

        // Initialize JwtUtil with mocked properties
        jwtUtil = new JwtUtil(jwtProperties);

        // Create a mock UserDetails object
        userDetails = new User(TEST_EMAIL, "password", Collections.emptyList()); // Mocked UserDetails
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken(TEST_EMAIL, TEST_ROLE);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken(TEST_EMAIL, TEST_ROLE);

        // ✅ Fix: Pass UserDetails instead of String
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtUtil.generateToken(TEST_EMAIL, TEST_ROLE);

        assertEquals(TEST_EMAIL, jwtUtil.extractUsername(token));
    }

    @Test
    void shouldExtractRole() {
        String token = jwtUtil.generateToken(TEST_EMAIL, TEST_ROLE);

        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals(TEST_ROLE.name(), claims.get("role"));
    }
}
