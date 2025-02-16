package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.User;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // This is now handled by MockitoExtension, no need for explicit annotation opening
    }

    @Test
    void shouldCreateWalletSuccessfully() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.existsByUserIdAndCurrency(1L, "USD")).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet(user, "USD", BigDecimal.ZERO));

        Wallet wallet = walletService.createWallet(1L, "USD");

        assertNotNull(wallet);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        WalletException exception = assertThrows(WalletException.class, () -> walletService.createWallet(1L, "USD"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldGetBalanceSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("200.00"));
        wallet.setCurrency("USD");

        when(walletRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(1L, "USD");

        assertEquals(new BigDecimal("200.00"), balance);
    }

    @Test
    void shouldThrowExceptionIfWalletNotFoundForBalance() {
        when(walletRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(Optional.empty());

        WalletException exception = assertThrows(WalletException.class, () -> walletService.getBalance(1L, "USD"));

        assertEquals("Wallet not found for currency USD", exception.getMessage());
    }

    @Test
    void shouldCreditWalletSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCurrency("USD");

        when(walletRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(Optional.of(wallet));

        walletService.creditWallet(wallet, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void shouldDebitWalletSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCurrency("USD");

        when(walletRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(Optional.of(wallet));

        walletService.debitWallet(wallet, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }
}