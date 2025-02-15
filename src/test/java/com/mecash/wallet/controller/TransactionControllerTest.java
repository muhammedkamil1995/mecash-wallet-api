package com.mecash.wallet.controller;


import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.service.TransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        // Mocking the transaction service behavior
        when(transactionService.createTransaction(anyLong(), any(BigDecimal.class), anyString(), null))
                .thenReturn(new Transaction());

        // Sending a mock request
        mockMvc.perform(post("/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"walletId\": 1, \"amount\": 50.00, \"transactionType\": \"CREDIT\"}"))
                .andExpect(status().isCreated()); // Changed to isCreated() for a proper HTTP status

        // Verify service call
        verify(transactionService, times(1)).createTransaction(anyLong(), any(BigDecimal.class), anyString(), null);
    }
}
