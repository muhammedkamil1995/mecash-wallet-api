package com.mecash.wallet.controller;

import com.mecash.wallet.service.WalletService;
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
    public ResponseEntity<String> createWallet(@PathVariable Long userId) {
        walletService.createWallet(userId);
        return ResponseEntity.ok("Wallet created successfully");
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<String> creditWallet(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {  // ✅ Removed currency parameter
        walletService.creditWallet(userId, amount);
        return ResponseEntity.ok("Wallet credited successfully");
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<String> debitWallet(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {  // ✅ Removed currency parameter
        walletService.debitWallet(userId, amount);
        return ResponseEntity.ok("Wallet debited successfully");
    }
}
