package com.mecash.wallet.repository;

import com.mecash.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Find all wallets for a user
    List<Wallet> findByUserId(Long userId);

    // Find a single wallet by user ID and currency
    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);

    // Find all wallets for a user with a specific currency
    List<Wallet> findAllByUserIdAndCurrency(Long userId, String currency);

    // Check if a user has a wallet for a specific currency
    boolean existsByUserIdAndCurrency(Long userId, String currency);

    // Check if a user has any wallet
    boolean existsByUserId(Long userId);
}