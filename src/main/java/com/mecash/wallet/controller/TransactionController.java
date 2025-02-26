package com.mecash.wallet.controller;

import com.mecash.wallet.dto.TransactionRequest;
import com.mecash.wallet.dto.TransactionResponse;
import com.mecash.wallet.model.User;
import com.mecash.wallet.security.JwtUtil;
import com.mecash.wallet.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    private static final String REQUEST_BODY_MISSING_MESSAGE = "Request body is missing.";
    private static final String INVALID_TYPE_MESSAGE = "Invalid transaction type for this endpoint: ";

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/deposit")
    @Transactional
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest request,
                                                      @RequestHeader("Authorization") String authHeader) {
        User user = validateTokenAndGetUser(authHeader);
        if (request == null || request.getWalletId() == null || request.getAmount() == null || request.getCurrency() == null) {
            logger.warn("Invalid deposit request: {}", request);
            return ResponseEntity.badRequest().body(new TransactionResponse(REQUEST_BODY_MISSING_MESSAGE, null));
        }
        // Validate type if provided, otherwise set to DEPOSIT
        if (request.getType() != null && !request.getType().equalsIgnoreCase("DEPOSIT")) {
            logger.warn("Invalid type {} for deposit endpoint", request.getType());
            return ResponseEntity.badRequest().body(new TransactionResponse(INVALID_TYPE_MESSAGE + request.getType(), null));
        }
        request.setType("DEPOSIT");
        TransactionResponse response = transactionService.processTransaction(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    @Transactional
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        User user = validateTokenAndGetUser(authHeader);
        if (request == null || request.getWalletId() == null || request.getAmount() == null || request.getCurrency() == null) {
            logger.warn("Invalid withdraw request: {}", request);
            return ResponseEntity.badRequest().body(new TransactionResponse(REQUEST_BODY_MISSING_MESSAGE, null));
        }
        // Validate type if provided, otherwise set to WITHDRAWAL
        if (request.getType() != null && !request.getType().equalsIgnoreCase("WITHDRAWAL")) {
            logger.warn("Invalid type {} for withdraw endpoint", request.getType());
            return ResponseEntity.badRequest().body(new TransactionResponse(INVALID_TYPE_MESSAGE + request.getType(), null));
        }
        request.setType("WITHDRAWAL");
        TransactionResponse response = transactionService.processTransaction(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransactionRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        User user = validateTokenAndGetUser(authHeader);
        if (request == null || request.getWalletId() == null || request.getRecipientWalletId() == null || 
            request.getAmount() == null || request.getCurrency() == null) {
            logger.warn("Invalid transfer request: {}", request);
            return ResponseEntity.badRequest().body(new TransactionResponse(REQUEST_BODY_MISSING_MESSAGE, null));
        }
        // Validate type if provided, otherwise set to TRANSFER
        if (request.getType() != null && !request.getType().equalsIgnoreCase("TRANSFER")) {
            logger.warn("Invalid type {} for transfer endpoint", request.getType());
            return ResponseEntity.badRequest().body(new TransactionResponse(INVALID_TYPE_MESSAGE + request.getType(), null));
        }
        request.setType("TRANSFER");
        TransactionResponse response = transactionService.processTransaction(user, request);
        return ResponseEntity.ok(response);
    }

    private User validateTokenAndGetUser(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String username = jwtUtil.extractUsername(token);
        User user = transactionService.getUserByUsername(username);
        if (!jwtUtil.validateToken(token, transactionService.loadUserByUsername(username))) {
            logger.warn("Invalid token for user: {}", username);
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return user;
    }
}