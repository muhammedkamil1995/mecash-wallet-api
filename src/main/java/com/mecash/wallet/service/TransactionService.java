package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SuppressWarnings("unused")
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionService(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    public String deposit(Long userId, BigDecimal amount, String currency) {
        walletService.creditWallet(userId, amount); // Added currency parameter

        Wallet wallet = walletService.findWalletByUserId(userId); // Updated method name

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transactionRepository.save(transaction);

        return "Deposit successful!";
    }

    @Transactional
    public String withdraw(Long userId, BigDecimal amount, String currency) {
        Wallet wallet = walletService.findWalletByUserId(userId); // Updated method name

        if (wallet.getBalance().compareTo(amount) < 0) {
            return "Insufficient balance!";
        }

        walletService.debitWallet(userId, amount); // Added currency parameter

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType("WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transactionRepository.save(transaction);

        return "Withdrawal successful!";
    }

    @Transactional
    public String transfer(Long senderId, Long recipientId, BigDecimal amount, String currency) {
        Wallet senderWallet = walletService.findWalletByUserId(senderId); // Updated method name

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            return "Insufficient balance!";
        }

        walletService.debitWallet(senderId, amount); // Added currency parameter
        walletService.creditWallet(recipientId, amount); // Added currency parameter

        Transaction transaction = new Transaction();
        transaction.setWallet(senderWallet);
        transaction.setType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transactionRepository.save(transaction);

        return "Transfer successful!";
    }

    @Transactional
    public Transaction createTransaction(Long userId, BigDecimal amount, String currency, String type) {
        Wallet wallet = walletService.findWalletByUserId(userId); // Updated method name

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        return transactionRepository.save(transaction);
    }
}
