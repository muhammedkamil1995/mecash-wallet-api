package com.mecash.wallet.controller;

import com.mecash.wallet.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")  // Enable CORS for all origins, i can later just restrict it this for frontend's domain
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long userId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.deposit(userId, amount, currency);
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long userId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.withdraw(userId, amount, currency);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long senderId, @RequestParam Long recipientId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.transfer(senderId, recipientId, amount, currency);
    }
}
