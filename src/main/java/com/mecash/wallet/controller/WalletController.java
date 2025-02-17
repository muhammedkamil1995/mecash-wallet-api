package com.mecash.wallet.controller;

import com.mecash.wallet.service.WalletService;
import com.mecash.wallet.model.Wallet; // Add this import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{userId}/create")
    public ResponseEntity<String> createWallet(
            @PathVariable Long userId,
            @RequestParam String currency) {
        walletService.createWallet(userId, currency);
        return ResponseEntity.ok("Wallet created successfully for currency: " + currency);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long userId,
            @RequestParam String currency) {
        return ResponseEntity.ok(walletService.getBalance(userId, currency));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<String> creditWallet(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String currency) {
        Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
        walletService.creditWallet(wallet, amount);
        return ResponseEntity.ok("Wallet credited successfully for currency: " + currency);
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<String> debitWallet(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String currency) {
        Wallet wallet = walletService.findWalletByUserIdAndCurrency(userId, currency);
        walletService.debitWallet(wallet, amount);
        return ResponseEntity.ok("Wallet debited successfully for currency: " + currency);
    }
}
