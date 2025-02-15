package com.mecash.wallet.controller;

import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.exception.InvalidRoleException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            RoleType roleType = RoleType.valueOf(request.getRole().toUpperCase());
            return jwtUtil.generateToken(request.getUsername(), roleType);
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role type: " + request.getRole());
        }
    }

    @GetMapping("/validate")
    public TokenValidationResponse validate(@RequestParam String token, @RequestParam String username) {
        boolean isValid = jwtUtil.validateToken(token, username);
        return new TokenValidationResponse(isValid, isValid ? "Token is valid" : "Invalid token");
    }

    @GetMapping("/refresh")
    public String refresh(@RequestParam String token) {
        return jwtUtil.refreshToken(token);
    }

    // Inner class for LoginRequest
    static class LoginRequest {
        private String username;
        private String password;
        private String role;

        public LoginRequest() {}

        public LoginRequest(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // Inner class for TokenValidationResponse
    static class TokenValidationResponse {
        private boolean valid;
        private String message;

        public TokenValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}