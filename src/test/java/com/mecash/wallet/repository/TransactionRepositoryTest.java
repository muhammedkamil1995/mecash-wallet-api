package com.mecash.wallet.repository;

import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldFindTransactionsByWalletId() {
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transactionRepository.save(transaction);

        List<Transaction> transactions = transactionRepository.findByWalletId(wallet.getId());

        assertEquals(1, transactions.size());
    }
}
