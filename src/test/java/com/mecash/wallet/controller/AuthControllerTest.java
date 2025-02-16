package com.mecash.wallet.controller;

import com.mecash.wallet.dto.AuthResponse;
import com.mecash.wallet.dto.LoginRequest;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
void shouldReturnTokenOnSuccessfulLogin() throws Exception {
    when(authService.login(any(LoginRequest.class)))
            .thenReturn(new AuthResponse("mocked-jwt-token", "Login successful"));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\": \"test@example.com\", \"password\": \"password123\", \"role\": \"USER\"}")) // ✅ Now matches LoginRequest
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
            .andExpect(jsonPath("$.message").value("Login successful"));
}

    
}
