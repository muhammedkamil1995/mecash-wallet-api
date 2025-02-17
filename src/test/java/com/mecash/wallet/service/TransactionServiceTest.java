package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepository, walletService);
    }

    @Test
    void testDepositSuccess() throws WalletException {
        // Arrange
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        when(walletService.findWalletByUserIdAndCurrency(anyLong(), anyString())).thenReturn(wallet);
        doNothing().when(walletService).creditWallet(any(Wallet.class), any(BigDecimal.class));
        Transaction transaction = new Transaction(wallet, "DEPOSIT", BigDecimal.TEN);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        String result = transactionService.deposit(1L, BigDecimal.TEN, "USD");

        // Assert
        assertEquals("Deposit successful!", result);
        verify(walletService).creditWallet(wallet, BigDecimal.TEN);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testWithdrawSuccess() throws WalletException {
        // Arrange
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));
        when(walletService.findWalletByUserIdAndCurrency(anyLong(), anyString())).thenReturn(wallet);
        doNothing().when(walletService).debitWallet(any(Wallet.class), any(BigDecimal.class));
        Transaction transaction = new Transaction(wallet, "WITHDRAWAL", BigDecimal.TEN);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        String result = transactionService.withdraw(1L, BigDecimal.TEN, "USD");

        // Assert
        assertEquals("Withdrawal successful!", result);
        verify(walletService).debitWallet(wallet, BigDecimal.TEN);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testTransferSuccess() throws WalletException {
        // Arrange
        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setBalance(BigDecimal.valueOf(100));
        Wallet recipientWallet = new Wallet();
        recipientWallet.setId(2L);
        when(walletService.findWalletByUserIdAndCurrency(eq(1L), anyString())).thenReturn(senderWallet);
        when(walletService.findWalletByUserIdAndCurrency(eq(2L), anyString())).thenReturn(recipientWallet);
        doNothing().when(walletService).debitWallet(any(Wallet.class), any(BigDecimal.class));
        doNothing().when(walletService).creditWallet(any(Wallet.class), any(BigDecimal.class));
        Transaction transaction = new Transaction(senderWallet, "TRANSFER", BigDecimal.TEN);
        transaction.setRecipientWallet(recipientWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        String result = transactionService.transfer(1L, 2L, BigDecimal.TEN, "USD");

        // Assert
        assertEquals("Transfer successful!", result);
        verify(walletService).debitWallet(senderWallet, BigDecimal.TEN);
        verify(walletService).creditWallet(recipientWallet, BigDecimal.TEN);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionSuccess() throws WalletException {
        // Arrange
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        when(walletService.findWalletByUserIdAndCurrency(anyLong(), anyString())).thenReturn(wallet);
        Transaction transaction = new Transaction(wallet, "DEPOSIT", BigDecimal.TEN);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = transactionService.createTransaction(1L, BigDecimal.TEN, "USD", "DEPOSIT");

        // Assert
        assertNotNull(result);
        assertEquals("DEPOSIT", result.getType());
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Additional tests for failure scenarios can be added here, like insufficient balance, etc.
}