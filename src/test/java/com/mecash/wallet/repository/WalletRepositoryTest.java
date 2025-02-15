package com.mecash.wallet.repository;

import com.mecash.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldFindWalletByUserId() {
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);

        Optional<Wallet> foundWallet = walletRepository.findByUserId(wallet.getUser().getId());

        assertTrue(foundWallet.isPresent());
    }
}
