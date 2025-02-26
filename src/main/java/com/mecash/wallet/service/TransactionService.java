package com.mecash.wallet.service;

import com.mecash.wallet.dto.TransactionRequest;
import com.mecash.wallet.dto.TransactionResponse;
import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.Transaction;
import com.mecash.wallet.model.User;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.TransactionRepository;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.repository.WalletRepository;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // Success message template
    private static final String SUCCESS_MESSAGE = "%s successful!";
    // Error message templates
    private static final String FAILED_DUE_TO = "%s failed due to: %s";
    private static final String UNEXPECTED_ERROR = "%s failed due to an unexpected error: %s";
    private static final String MINIMUM_AMOUNT_ERROR = "%s amount must be at least 0.01";
    private static final String INSUFFICIENT_BALANCE = "Insufficient balance for %s in wallet: %d";
    private static final String WALLET_NOT_FOUND = "Wallet not found: %d";
    private static final String UNAUTHORIZED_ACCESS = "Unauthorized wallet access for wallet: %d";
    private static final String CURRENCY_MISMATCH = "Currency mismatch for wallet %d: expected %s, found %s";
    private static final String RECIPIENT_NOT_FOUND = "Recipient wallet not found: %d";
    private static final String RECIPIENT_REQUIRED = "Recipient wallet ID required for transfer";

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, 
                              WalletRepository walletRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse processTransaction(User user, TransactionRequest request) {
        String action = request.getType();
        try {
            BigDecimal amount = request.getAmount();
            if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) {
                throw new WalletException(String.format(MINIMUM_AMOUNT_ERROR, action));
            }
            Wallet wallet = getAndValidateWallet(user, request.getWalletId(), request.getCurrency());
            Transaction transaction = new Transaction(wallet, user, action, amount, request.getCurrency(), null);

            processTransactionType(wallet, transaction, request);
            walletRepository.save(wallet);
            Transaction savedTransaction = transactionRepository.save(transaction);

            logger.info("{} successful for wallet {}: {} {}", action, request.getWalletId(), amount, request.getCurrency());
            return new TransactionResponse(String.format(SUCCESS_MESSAGE, action), savedTransaction.getId());
        } catch (WalletException e) {
            logger.error("{} failed for wallet {}: {}", action, request.getWalletId(), e.getMessage());
            return new TransactionResponse(String.format(FAILED_DUE_TO, action, e.getMessage()), null);
        } catch (Exception e) {
            logger.error("Unexpected error during {} for wallet {}: {}", action, request.getWalletId(), e.getMessage(), e);
            return new TransactionResponse(String.format(UNEXPECTED_ERROR, action, e.getMessage()), null);
        }
    }

    private void processTransactionType(Wallet wallet, Transaction transaction, TransactionRequest request) {
        String action = request.getType().toUpperCase();
        switch (action) {
            case "DEPOSIT":
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                break;
            case "WITHDRAWAL":
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new WalletException(String.format(INSUFFICIENT_BALANCE, action.toLowerCase(), request.getWalletId()));
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                break;
            case "TRANSFER":
                if (request.getRecipientWalletId() == null) {
                    throw new WalletException(RECIPIENT_REQUIRED);
                }
                Wallet recipientWallet = walletRepository.findById(request.getRecipientWalletId())
                    .orElseThrow(() -> new WalletException(String.format(RECIPIENT_NOT_FOUND, request.getRecipientWalletId())));
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new WalletException(String.format(INSUFFICIENT_BALANCE, action.toLowerCase(), request.getWalletId()));
                }
                if (!wallet.getCurrency().equalsIgnoreCase(recipientWallet.getCurrency())) {
                    throw new WalletException(String.format(CURRENCY_MISMATCH, request.getWalletId(), request.getCurrency(), wallet.getCurrency()));
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                recipientWallet.setBalance(recipientWallet.getBalance().add(request.getAmount()));
                transaction.setRecipientWallet(recipientWallet);
                walletRepository.save(recipientWallet);
                break;
            default:
                throw new WalletException("Invalid transaction type: " + action);
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.warn("User not found: {}", username);
                return new IllegalArgumentException("User not found");
            });
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles().stream().map(role -> role.getName().name()).toArray(String[]::new))
            .build();
    }

    private Wallet getAndValidateWallet(User user, Long walletId, String currency) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletException(String.format(WALLET_NOT_FOUND, walletId)));
        if (!wallet.getUser().getId().equals(user.getId())) {
            throw new WalletException(String.format(UNAUTHORIZED_ACCESS, walletId));
        }
        if (!wallet.getCurrency().equalsIgnoreCase(currency)) {
            throw new WalletException(String.format(CURRENCY_MISMATCH, walletId, currency, wallet.getCurrency()));
        }
        return wallet;
    }

    public String deposit(long l, BigDecimal ten, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deposit'");
    }

    public String withdraw(long l, BigDecimal ten, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }

    public String transfer(long l, long m, BigDecimal ten, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'transfer'");
    }

    public Transaction createTransaction(long l, BigDecimal ten, String string, String string2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTransaction'");
    }
}