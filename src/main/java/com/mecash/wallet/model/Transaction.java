package com.mecash.wallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type; // "DEPOSIT", "WITHDRAWAL", "TRANSFER"

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "recipient_wallet_id", nullable = true) // Nullable for non-transfer transactions
    private Wallet recipientWallet; // Changed from User to Wallet

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Default Constructor (Required by JPA)
    public Transaction() {}

    // Full Constructor for all transactions including transfers
    public Transaction(Wallet wallet, User user, String type, BigDecimal amount, String currency, Wallet recipientWallet) {
        this.wallet = wallet;
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.recipientWallet = recipientWallet;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for deposits/withdrawals (no recipient wallet)
    public Transaction(Wallet wallet, String type, BigDecimal amount) {
        this.wallet = wallet;
        this.user = wallet.getUser(); // Automatically set the user from the wallet
        this.type = type;
        this.amount = amount;
        this.currency = wallet.getCurrency(); // Ensure currency is correctly set
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Wallet getRecipientWallet() { return recipientWallet; }
    public void setRecipientWallet(Wallet recipientWallet) { this.recipientWallet = recipientWallet; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}