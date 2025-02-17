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
    public String deposit(Long userId, BigDecimal amount, String currency) throws WalletException {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
            walletService.creditWallet(wallet, amount);

            Transaction transaction = new Transaction(wallet, "DEPOSIT", amount);
            transactionRepository.save(transaction);

            logger.info("Deposit successful for user {}: {} {}", userId, amount, currency);
            return "Deposit successful!";
        } catch (WalletException e) {
            // Log only, do not rethrow
            logger.error("WalletException during deposit for user {}: {}", userId, e.getMessage(), e);
            return "Deposit failed due to: " + e.getMessage();
        } catch (Exception e) {
            // Log and handle within the method, do not rethrow
            logger.error("Unexpected error during deposit for user {}: {}", userId, e.getMessage(), e);
            return "Deposit failed due to an unexpected error.";
        }
    }

    @Transactional
    public String withdraw(Long userId, BigDecimal amount, String currency) throws WalletException {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new WalletException("Insufficient balance for withdrawal. User ID: " + userId);
            }
            walletService.debitWallet(wallet, amount);

            Transaction transaction = new Transaction(wallet, "WITHDRAWAL", amount);
            transactionRepository.save(transaction);

            logger.info("Withdrawal successful for user {}: {} {}", userId, amount, currency);
            return "Withdrawal successful!";
        } catch (WalletException e) {
            // Log only, do not rethrow
            logger.error("WalletException during withdrawal for user {}: {}", userId, e.getMessage(), e);
            return "Withdrawal failed due to: " + e.getMessage();
        } catch (Exception e) {
            // Log and handle within the method, do not rethrow
            logger.error("Unexpected error during withdrawal for user {}: {}", userId, e.getMessage(), e);
            return "Withdrawal failed due to an unexpected error.";
        }
    }

    @Transactional
    public String transfer(Long senderId, Long recipientId, BigDecimal amount, String currency) throws WalletException {
        try {
            Wallet senderWallet = walletService.findWalletByUserIdAndCurrency(senderId, currency);
            Wallet recipientWallet = walletService.findWalletByUserIdAndCurrency(recipientId, currency);

            if (senderWallet.getBalance().compareTo(amount) < 0) {
                throw new WalletException("Insufficient balance for transfer. Sender ID: " + senderId);
            }

            walletService.debitWallet(senderWallet, amount);
            walletService.creditWallet(recipientWallet, amount);

            Transaction transaction = new Transaction(senderWallet, "TRANSFER", amount);
            transaction.setRecipientWallet(recipientWallet);
            transactionRepository.save(transaction);

            logger.info("Transfer successful from user {} to user {}: {} {}", senderId, recipientId, amount, currency);
            return "Transfer successful!";
        } catch (WalletException e) {
            // Log only, do not rethrow
            logger.error("WalletException during transfer from user {} to user {}: {}", senderId, recipientId, e.getMessage(), e);
            return "Transfer failed due to: " + e.getMessage();
        } catch (Exception e) {
            // Log and handle within the method, do not rethrow
            logger.error("Unexpected error during transfer from user {} to user {}: {}", senderId, recipientId, e.getMessage(), e);
            return "Transfer failed due to an unexpected error.";
        }
    }

    @Transactional
    public Transaction createTransaction(Long userId, BigDecimal amount, String currency, String type) throws WalletException {
        try {
            Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);

            Transaction transaction;
            if ("TRANSFER".equalsIgnoreCase(type)) {
                throw new WalletException("Transfer transactions require a recipient. Use the transfer method instead.");
            } else {
                transaction = new Transaction(wallet, type, amount);
            }
            return transactionRepository.save(transaction);
        } catch (WalletException e) {
            // Log only, do not rethrow
            logger.error("WalletException during transaction creation for user {}: {}", userId, e.getMessage(), e);
            return null; // Or handle this case differently based on your needs
        } catch (Exception e) {
            // Log and handle within the method, do not rethrow
            logger.error("Unexpected error during transaction creation for user {}: {}", userId, e.getMessage(), e);
            return null; // Or handle this case differently based on your needs
        }
    }
}