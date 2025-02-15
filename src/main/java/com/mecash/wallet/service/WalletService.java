package com.mecash.wallet.service;

import com.mecash.wallet.exception.WalletException;
import com.mecash.wallet.model.User;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.WalletRepository;
import com.mecash.wallet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String WALLET_NOT_FOUND = "Wallet not found";
    private static final String USER_ALREADY_HAS_WALLET = "User already has a wallet";
    private static final String INSUFFICIENT_BALANCE = "Insufficient balance";

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Wallet createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WalletException(USER_NOT_FOUND));

        if (walletRepository.existsByUserId(userId)) {
            throw new WalletException(USER_ALREADY_HAS_WALLET);
        }

        
        Wallet wallet = new Wallet(user, "USD", BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    public BigDecimal getBalance(Long userId) {
        return findWalletByUserId(userId).getBalance();
    }

    @Transactional
    public void creditWallet(Long userId, BigDecimal amount) {
        Wallet wallet = findWalletByUserId(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Transactional
    public void debitWallet(Long userId, BigDecimal amount) {
        Wallet wallet = findWalletByUserId(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new WalletException(INSUFFICIENT_BALANCE);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    public Wallet findWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
    }
}
