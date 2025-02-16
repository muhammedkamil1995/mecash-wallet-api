package com.mecash.wallet.dto;

import java.math.BigDecimal;

public class TransactionDTO {
    private Long userId;
    private Long recipientId; 
    private BigDecimal amount;
    private String currency;
    private String type; 

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(Long userId, Long recipientId, BigDecimal amount, String currency, String type) {
        this.userId = userId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
