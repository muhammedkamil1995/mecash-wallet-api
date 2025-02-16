package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.User;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.WalletRepository;
import com.mecash.wallet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String WALLET_NOT_FOUND = "Wallet not found";
    private static final String INSUFFICIENT_BALANCE = "Insufficient balance";

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Wallet createWallet(Long userId, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WalletException(USER_NOT_FOUND));

        if (walletRepository.existsByUserIdAndCurrency(userId, currency)) {
            throw new WalletException("User already has a wallet for this currency");
        }

        Wallet wallet = new Wallet(user, currency, BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    public BigDecimal getBalance(Long userId, String currency) {
        return findWalletByUserIdAndCurrency(userId, currency).getBalance();
    }

    @Transactional
    public void creditWallet(Wallet wallet, BigDecimal amount) {
        if (wallet == null) {
            throw new WalletException(WALLET_NOT_FOUND);
        }
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Transactional
    public void debitWallet(Wallet wallet, BigDecimal amount) {
        if (wallet == null) {
            throw new WalletException(WALLET_NOT_FOUND);
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new WalletException(INSUFFICIENT_BALANCE);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    public Wallet findWalletByUserIdAndCurrency(Long userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND + " for currency " + currency));
    }

    public List<Wallet> findWalletsByUserIdAndCurrency(Long userId, String currency) {
        return walletRepository.findAllByUserIdAndCurrency(userId, currency);
    }

    public Wallet findWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
    }
}