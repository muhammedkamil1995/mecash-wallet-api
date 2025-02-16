package com.mecash.wallet.controller;

import com.mecash.wallet.model.Role;
import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.model.User;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.repository.RoleRepository;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.service.JwtUserDetailsService;
import com.mecash.wallet.dto.RegisterRequest;
import com.mecash.wallet.exception.InvalidRoleException;
import com.mecash.wallet.utils.JWTDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if the username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        // Check if the email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }

        // Fetch user role from the database (assuming "USER" by default)
        RoleType defaultRoleType = RoleType.USER; // Use the correct enum value
        Role userRole = roleRepository.findByName(defaultRoleType)
            .orElseThrow(() -> new RuntimeException("Default role not found"));

        // Create new user
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.getRoles().add(userRole);  // Assign default role

        // Save the new user and commit the transaction
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            RoleType roleType = RoleType.valueOf(request.getRole().toUpperCase());
            String token = jwtUtil.generateToken(request.getUsername(), roleType);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role type: " + request.getRole());
        }
    }

    /**
     * Validate JWT token.
     */
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

    // Static inner class for TokenValidationResponse
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
