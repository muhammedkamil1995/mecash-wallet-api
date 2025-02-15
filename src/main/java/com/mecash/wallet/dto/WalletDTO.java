package com.mecash.wallet.dto;

import java.math.BigDecimal;

public class WalletDTO {
    private String username;
    private BigDecimal balance;

    public WalletDTO(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
