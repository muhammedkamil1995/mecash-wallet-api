package com.mecash.wallet.dto;

import jakarta.validation.constraints.NotNull;

public class WalletCreateRequestDTO {

    @NotNull(message = "Currency is required")
    private String currency;

    public WalletCreateRequestDTO() {}

    public WalletCreateRequestDTO(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
