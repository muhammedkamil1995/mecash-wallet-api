package com.mecash.wallet.controller;

import com.mecash.wallet.dto.RegisterRequest;
import com.mecash.wallet.model.Role;
import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.model.User;
import com.mecash.wallet.repository.RoleRepository;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.service.JwtUserDetailsService;
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
    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, JwtUserDetailsService userDetailsService,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // Validation: Check for missing fields
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null || request.getRole() == null) {
            return ResponseEntity.badRequest().body("Missing required fields: username, email, password, and role are required.");
        }

        // Check if username is already taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        // Check if email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }

        // Get or create the default role
        RoleType roleType;
        try {
            roleType = RoleType.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role provided. Available roles are: USER, ADMIN.");
        }

        Role userRole = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Create and save the user
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.getRoles().add(userRole);

        logger.info("Role '{}' added to user '{}'", userRole.getName(), newUser.getUsername());

        User savedUser = userRepository.save(newUser);
        logger.info("User saved with id: {}", savedUser.getId());

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Validation: Check for missing fields
        if (request.getUsername() == null || request.getPassword() == null || request.getRole() == null) {
            return ResponseEntity.badRequest().body("Missing required fields: username, password, and role are required.");
        }

        try {
            RoleType roleType = RoleType.valueOf(request.getRole().toUpperCase());
            String token = jwtUtil.generateToken(request.getUsername(), roleType);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role type: " + request.getRole());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(@RequestParam String token, @RequestParam String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
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

    // Static inner class for LoginRequest
    @Data
    static class LoginRequest {
        private String username;
        private String password;
        private String role;
    }

    // Static inner class for TokenValidationResponse
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
