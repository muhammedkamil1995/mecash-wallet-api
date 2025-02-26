package com.mecash.wallet.repository;

import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository; // Ensure you have this repository for saving users

    @Test
    void shouldFindWalletByUserId() {
        // Create and save a user first
        User user = new User();
        user.setUsername("Test User");
        user = userRepository.save(user); // Save and get the persisted user

        // Create and save a wallet linked to the user
        Wallet wallet = new Wallet();
        wallet.setUser(user); // Associate wallet with the user
        walletRepository.save(wallet);

        // Fetch wallets for the user
        List<Wallet> foundWallets = walletRepository.findByUserId(user.getId());

        // Assert that at least one wallet exists for the user
        assertFalse(foundWallets.isEmpty(), "No wallets found for the given user ID");
    }
}
