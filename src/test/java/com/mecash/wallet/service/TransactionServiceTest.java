package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.TransactionRepository;
import com.mecash.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("100.00"));

        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        Transaction createdTransaction = transactionService.createTransaction(1L, new BigDecimal("50.00"), "CREDIT", null);

        assertNotNull(createdTransaction);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Extract method invocation into an executable variable
        Executable executable = () -> transactionService.createTransaction(1L, new BigDecimal("50.00"), "CREDIT", null);

        WalletException exception = assertThrows(WalletException.class, executable);

        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInsufficientFunds() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("30.00")); // Less than the amount to debit

        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(wallet));

        // Extract method invocation into an executable variable
        Executable executable = () -> transactionService.createTransaction(1L, new BigDecimal("50.00"), "DEBIT", null);

        WalletException exception = assertThrows(WalletException.class, executable);

        assertEquals("Insufficient funds", exception.getMessage());
    }
}
