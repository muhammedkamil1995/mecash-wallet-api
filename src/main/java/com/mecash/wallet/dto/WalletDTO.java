package com.mecash.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletDTO {
    private Long id;              // Unique wallet identifier
    private String username;      // User context (optional)
    private String currency;      // Currency of the wallet
    private BigDecimal balance;   // Balance of the wallet

    public WalletDTO(Long id, String username, String currency, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.currency = currency;
        this.balance = balance;
    }
}