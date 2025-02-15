package com.mecash.wallet.controller;

import com.mecash.wallet.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller for handling transaction-related operations such as deposits, withdrawals, and transfers.
 * All endpoints expect data via query parameters.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructor for TransactionController.
     * 
     * @param transactionService The service handling the transaction logic.
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Endpoint to deposit money into a user's wallet.
     * 
     * @param userId The ID of the user whose wallet will receive the deposit.
     * @param amount The amount to deposit.
     * @param currency The currency of the deposit.
     * @return A message indicating the result of the operation.
     */
    @PostMapping("/deposit")
    public String deposit(@RequestParam Long userId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.deposit(userId, amount, currency);
    }

    /**
     * Endpoint to withdraw money from a user's wallet.
     * 
     * @param userId The ID of the user whose wallet will be debited.
     * @param amount The amount to withdraw.
     * @param currency The currency of the withdrawal.
     * @return A message indicating the result of the operation.
     */
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long userId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.withdraw(userId, amount, currency);
    }

    /**
     * Endpoint to transfer money between two users' wallets.
     * 
     * @param senderId The ID of the user sending the money.
     * @param recipientId The ID of the user receiving the money.
     * @param amount The amount to transfer.
     * @param currency The currency of the transfer.
     * @return A message indicating the result of the operation.
     */
    @PostMapping("/transfer")
    public String transfer(@RequestParam Long senderId, @RequestParam Long recipientId, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return transactionService.transfer(senderId, recipientId, amount, currency);
    }
}