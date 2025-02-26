package com.mecash.wallet.dto;

import lombok.Data;

@Data
public class WalletResponse {
    private String message;

    public WalletResponse(String message) {
        this.message = message;
    }
}