package com.mecash.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private Long walletId;         // Source wallet for all transactions
    private Long recipientWalletId; // Required for TRANSFER
    private String type;           // DEPOSIT, WITHDRAWAL, TRANSFER
    private BigDecimal amount;     // Amount as BigDecimal for precision
    private String currency;
}