package com.mecash.wallet.dto;

import lombok.Data;

@Data
public class TransactionResponse {
    private String message;
    private Long transactionId;

    public TransactionResponse(String message, Long transactionId) {
        this.message = message;
        this.transactionId = transactionId;
    }
}