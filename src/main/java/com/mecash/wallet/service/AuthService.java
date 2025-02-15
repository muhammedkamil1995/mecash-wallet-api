package com.mecash.wallet.service;

import com.mecash.wallet.dto.AuthResponse;
import com.mecash.wallet.dto.LoginRequest;
import com.mecash.wallet.exception.UserNotFoundException;
import com.mecash.wallet.model.Role;
import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.model.User;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.logging.Logger;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User with email " + request.getEmail() + " not found."));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warning("Failed login attempt for user: " + request.getEmail());
                return new AuthResponse(null, "Invalid credentials. Please try again.");
            }

            
            Set<Role> roles = user.getRoles();
            if (roles.isEmpty()) {
                logger.severe("User " + request.getEmail() + " has no role assigned.");
                return new AuthResponse(null, "User role is not assigned. Please contact support.");
            }

            
            RoleType primaryRole = roles.iterator().next().getName(); 

            
            String token = jwtUtil.generateToken(user.getEmail(), primaryRole);
            return new AuthResponse(token, "Login successful");

        } catch (Exception ex) {
            logger.severe("Login failed: " + ex.getMessage());
            return new AuthResponse(null, "An unexpected error occurred. Please try again later.");
        }
    }
}
