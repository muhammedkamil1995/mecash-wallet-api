package com.mecash.wallet.controller;

import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void shouldCreateWalletSuccessfully() throws Exception {
        // Since createWallet now takes a currency parameter, adjust the test accordingly
        when(walletService.createWallet(1L, "USD")).thenReturn(new Wallet());

        mockMvc.perform(post("/wallets/{userId}/create", 1L)
                .param("currency", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet created successfully for currency: USD"));

        verify(walletService, times(1)).createWallet(1L, "USD");
    }

    @Test
    void shouldGetWalletBalance() throws Exception {
        when(walletService.getBalance(1L, "USD")).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/wallets/{userId}/balance", 1L)
                .param("currency", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.00"));

        verify(walletService, times(1)).getBalance(1L, "USD");
    }

    @Test
    void shouldCreditWalletSuccessfully() throws Exception {
        when(walletService.findWalletByUserIdAndCurrency(1L, "USD")).thenReturn(new Wallet());

        mockMvc.perform(post("/wallets/{userId}/credit", 1L)
                .param("amount", "50.00")
                .param("currency", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet credited successfully for currency: USD"));
    }

    @Test
    void shouldDebitWalletSuccessfully() throws Exception {
        when(walletService.findWalletByUserIdAndCurrency(1L, "USD")).thenReturn(new Wallet());

        mockMvc.perform(post("/wallets/{userId}/debit", 1L)
                .param("amount", "30.00")
                .param("currency", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet debited successfully for currency: USD"));
    }
}