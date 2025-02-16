package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionService(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    public String deposit(Long userId, BigDecimal amount, String currency) {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
            walletService.creditWallet(wallet, amount);

            Transaction transaction = new Transaction();
            transaction.setWallet(wallet);
            transaction.setType("DEPOSIT");
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            transactionRepository.save(transaction);

            logger.info("Deposit successful for user {}: {} {}", userId, amount, currency);
            return "Deposit successful!";
        } catch (Exception e) {
            logger.error("Error during deposit for user {}: {}", userId, e.getMessage());
            throw new WalletException("An error occurred while processing the deposit.");
        }
    }

    @Transactional
    public String withdraw(Long userId, BigDecimal amount, String currency) {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new WalletException("Insufficient balance for user ID: " + userId);
            }
            walletService.debitWallet(wallet, amount);

            Transaction transaction = new Transaction();
            transaction.setWallet(wallet);
            transaction.setType("WITHDRAWAL");
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            transactionRepository.save(transaction);

            logger.info("Withdrawal successful for user {}: {} {}", userId, amount, currency);
            return "Withdrawal successful!";
        } catch (Exception e) {
            logger.error("Error during withdrawal for user {}: {}", userId, e.getMessage());
            throw new WalletException("An error occurred while processing the withdrawal.");
        }
    }

    @Transactional
    public String transfer(Long senderId, Long recipientId, BigDecimal amount, String currency) {
        try {
            Wallet senderWallet = walletService.findWalletByUserIdAndCurrency(senderId, currency);
            Wallet recipientWallet = walletService.findWalletByUserIdAndCurrency(recipientId, currency);

            if (senderWallet.getBalance().compareTo(amount) < 0) {
                throw new WalletException("Insufficient balance for sender ID: " + senderId);
            }

            walletService.debitWallet(senderWallet, amount);
            walletService.creditWallet(recipientWallet, amount);

            Transaction transaction = new Transaction();
            transaction.setWallet(senderWallet); // Assuming we log from the sender's perspective
            transaction.setType("TRANSFER");
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            transactionRepository.save(transaction);

            logger.info("Transfer successful from user {} to user {}: {} {}", senderId, recipientId, amount, currency);
            return "Transfer successful!";
        } catch (Exception e) {
            logger.error("Error during transfer from user {} to user {}: {}", senderId, recipientId, e.getMessage());
            throw new WalletException("An error occurred while processing the transfer.");
        }
    }

    @Transactional
    public Transaction createTransaction(Long userId, BigDecimal amount, String currency, String type) {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);

            Transaction transaction = new Transaction();
            transaction.setWallet(wallet);
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            logger.error("Error during transaction creation for user {}: {}", userId, e.getMessage());
            throw new WalletException("An error occurred while creating the transaction.");
        }
    }
}