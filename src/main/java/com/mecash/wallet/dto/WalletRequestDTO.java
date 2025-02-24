package com.mecash.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WalletRequestDTO {

    @Min(value = 0, message = "Amount must be a positive value")
    private BigDecimal amount; // 👈 Amount is now optional!

    @NotNull(message = "Currency is required")
    private String currency;

    public WalletRequestDTO() {}

    public WalletRequestDTO(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
