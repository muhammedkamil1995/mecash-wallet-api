package com.mecash.wallet.repository;

import com.mecash.wallet.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void shouldNotFindUserIfEmailDoesNotExist() {
        Optional<User> foundUser = userRepository.findByEmail("doesnotexist@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void shouldNotFindUserIfUsernameDoesNotExist() {
        Optional<User> foundUser = userRepository.findByUsername("unknownUser");

        assertFalse(foundUser.isPresent());
    }
}
