package com.mecash.wallet.controller;

import com.mecash.wallet.dto.TransactionRequest;
import com.mecash.wallet.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")  // Enable CORS for all origins
public class TransactionController {

    private final TransactionService transactionService;

    // Define a constant for the "Request body is missing" message
    private static final String REQUEST_BODY_MISSING_MESSAGE = "Request body is missing.";

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody TransactionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(REQUEST_BODY_MISSING_MESSAGE);
        }
        String response = transactionService.deposit(request.getUserId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody TransactionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(REQUEST_BODY_MISSING_MESSAGE);
        }
        String response = transactionService.withdraw(request.getUserId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransactionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(REQUEST_BODY_MISSING_MESSAGE);
        }
        String response = transactionService.transfer(request.getSenderId(), request.getRecipientId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok(response);
    }
}
