package com.mecash.wallet.service;

import com.mecash.wallet.dto.RegisterRequest;
import com.mecash.wallet.exception.UserAlreadyExistsException;
import com.mecash.wallet.model.User;
import com.mecash.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setName("New User");

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword("hashedPassword");
        newUser.setUsername(request.getName());

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals("New User", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotRegisterIfUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setName("Existing User");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(request));

        assertEquals("Email already exists!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
