package com.mecash.wallet.controller;

import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
        when(walletService.createWallet(1L)).thenReturn(new Wallet());

        mockMvc.perform(post("/wallet/create/1"))
                .andExpect(status().isOk());

        verify(walletService, times(1)).createWallet(1L);
    }

    @Test
    void shouldGetWalletBalance() throws Exception {
        when(walletService.getBalance(1L)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/wallet/balance/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.00"));

        verify(walletService, times(1)).getBalance(1L);
    }

    @Test
    void shouldCreditWalletSuccessfully() throws Exception {
        mockMvc.perform(post("/wallet/credit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk());

    }

    @Test
    void shouldDebitWalletSuccessfully() throws Exception {
        mockMvc.perform(post("/wallet/debit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 30.00}"))
                .andExpect(status().isOk());

    
    }
}
