package com.mecash.wallet.controller;

import com.mecash.wallet.dto.RegisterRequest;
import com.mecash.wallet.exception.UserAlreadyExistsException;
import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.model.User;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.service.UserService;
import com.mecash.wallet.utils.JWTDecoder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getEmail() == null || 
            request.getPassword() == null || request.getRole() == null) {
            logger.error("Missing required fields in request: {}", request);
            return ResponseEntity.badRequest().body(new RegisterResponse("Missing required fields", null));
        }

        try {
            request.setPassword(passwordEncoder.encode(request.getPassword())); // Always encode password
            User savedUser = userService.registerUser(request);
            RoleType roleType = RoleType.valueOf(request.getRole().toUpperCase());
            String token = jwtUtil.generateToken(savedUser.getUsername(), roleType);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return ResponseEntity.ok(new RegisterResponse("User registered successfully", token));
        } catch (UserAlreadyExistsException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new RegisterResponse(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role provided: {}", request.getRole());
            return ResponseEntity.badRequest().body(new RegisterResponse(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new RegisterResponse("Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null || request.getRole() == null) {
            logger.error("Missing required fields in request: {}", request);
            return ResponseEntity.badRequest().body(new LoginResponse("Missing required fields: username, password, and role are required", null));
        }

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role type: {}", request.getRole());
            return ResponseEntity.badRequest().body(new LoginResponse("Invalid role type: " + request.getRole(), null));
        }

        try {
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", request.getUsername());
                    return new RuntimeException("User not found with username: " + request.getUsername());
                });

            // Verify password - Allow both hashed & plain text
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword()) &&
                !request.getPassword().equals(user.getPassword())) {
                logger.warn("Invalid password for user: {}", request.getUsername());
                return ResponseEntity.badRequest().body(new LoginResponse("Invalid username or password", null));
            }

            String token = jwtUtil.generateToken(request.getUsername(), roleType);
            logger.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse("Login successful", token));

        } catch (RuntimeException e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new LoginResponse("Login failed: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new LoginResponse("Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(@RequestParam String token, @RequestParam String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        boolean isValid = jwtUtil.validateToken(token, userDetails);
        return ResponseEntity.ok(new TokenValidationResponse(isValid, isValid ? "Token is valid" : "Invalid token"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam String token) {
        return ResponseEntity.ok(jwtUtil.refreshToken(token));
    }

    @GetMapping("/decode")
    public ResponseEntity<String> decodeToken(@RequestParam String token) {
        try {
            return ResponseEntity.ok(JWTDecoder.decodeJWT(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
        }
    }

    @Data
    static class RegisterResponse {
        private String message;
        private String token;

        public RegisterResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }

    @Data
    static class LoginResponse {
        private String message;
        private String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
        private String role;
    }

    @Data
    static class TokenValidationResponse {
        private boolean valid;
        private String message;

        public TokenValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }
}
