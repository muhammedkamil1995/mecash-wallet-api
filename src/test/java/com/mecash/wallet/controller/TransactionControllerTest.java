package com.mecash.wallet.controller;

import com.mecash.wallet.dto.TransactionRequest;
import com.mecash.wallet.dto.TransactionResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        // Mocking service response
        TransactionResponse mockResponse = new TransactionResponse("Transaction successful", 1L);
        when(transactionService.processTransaction(any(), any(TransactionRequest.class))).thenReturn(mockResponse);

        // Sending a mock request
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "walletId": 1,
                      "amount": 50.00,
                      "currency": "USD",
                      "type": "DEPOSIT"
                    }
                    """))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.message").value("Transaction successful"))
                .andExpect(jsonPath("$.transactionId").value(1));

        // Verify service call
        verify(transactionService, times(1)).processTransaction(any(), any(TransactionRequest.class));
    }
}
