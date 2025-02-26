package com.mecash.wallet.controller;

import com.mecash.wallet.dto.WalletDTO;
import com.mecash.wallet.dto.WalletResponse;
import com.mecash.wallet.model.User;
import com.mecash.wallet.model.Wallet;
import com.mecash.wallet.repository.UserRepository;
import com.mecash.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    // Constants
    private static final String USER_NOT_FOUND = "User not found";
    private static final String WALLET_NOT_FOUND = "Wallet not found";
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String DEPOSIT_SUCCESSFUL = "Deposit successful";
    private static final String WITHDRAWAL_SUCCESSFUL = "Withdrawal successful";
    private static final String TRANSFER_SUCCESSFUL = "Transfer successful";
    private static final String CURRENCY_MISMATCH = "Wallets must use the same currency";
    private static final String UNAUTHORIZED_ATTEMPT_LOG = "Unauthorized %s attempt by {} on wallet {}";
    private static final String SUCCESS_LOG = "%s {} {} %s wallet {} for user {}";
    private static final String WITHDRAWAL_FAILED_LOG = "Withdrawal failed for wallet {}: {}";
    private static final String TRANSFER_FAILED_LOG = "Transfer failed from wallet {} to {}: {}";
    private static final String WALLET_NOT_FOUND_LOG = "Wallet not found: {}";

    public WalletController(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<WalletDTO>> getUserWallets() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new RuntimeException(USER_NOT_FOUND);
                });
        List<Wallet> wallets = walletRepository.findByUserId(user.getId());
        List<WalletDTO> walletDTOs = wallets.stream()
                .map(wallet -> new WalletDTO(wallet.getId(), wallet.getUser().getUsername(), wallet.getCurrency(), wallet.getBalance()))
                .toList();
        logger.info("Retrieved {} wallets for user: {}", walletDTOs.size(), username);
        return ResponseEntity.ok(walletDTOs);
    }

    @PostMapping("/{walletId}/deposit")
    @Transactional
    public ResponseEntity<WalletResponse> deposit(@PathVariable Long walletId, @RequestBody AmountRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.warn(WALLET_NOT_FOUND_LOG, walletId);
                    return new RuntimeException(WALLET_NOT_FOUND);
                });
        if (!wallet.getUser().getUsername().equals(username)) {
            logger.warn(UNAUTHORIZED_ATTEMPT_LOG, "deposit", username, walletId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WalletResponse(UNAUTHORIZED));
        }
        wallet.credit(request.getAmount());
        walletRepository.save(wallet);
        logger.info(SUCCESS_LOG, "Deposited", request.getAmount(), wallet.getCurrency(), "into", walletId, username);
        return ResponseEntity.ok(new WalletResponse(DEPOSIT_SUCCESSFUL));
    }

    @PostMapping("/{walletId}/withdraw")
    @Transactional
    public ResponseEntity<WalletResponse> withdraw(@PathVariable Long walletId, @RequestBody AmountRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.warn(WALLET_NOT_FOUND_LOG, walletId);
                    return new RuntimeException(WALLET_NOT_FOUND);
                });
        if (!wallet.getUser().getUsername().equals(username)) {
            logger.warn(UNAUTHORIZED_ATTEMPT_LOG, "withdrawal", username, walletId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WalletResponse(UNAUTHORIZED));
        }
        try {
            wallet.debit(request.getAmount());
            walletRepository.save(wallet);
            logger.info(SUCCESS_LOG, "Withdrew", request.getAmount(), wallet.getCurrency(), "from", walletId, username);
            return ResponseEntity.ok(new WalletResponse(WITHDRAWAL_SUCCESSFUL));
        } catch (IllegalStateException e) {
            logger.warn(WITHDRAWAL_FAILED_LOG, walletId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new WalletResponse(e.getMessage()));
        }
    }

    @PostMapping("/{fromWalletId}/transfer/{toWalletId}")
    @Transactional
    public ResponseEntity<WalletResponse> transfer(@PathVariable Long fromWalletId, @PathVariable Long toWalletId, @RequestBody AmountRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet fromWallet = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> {
                    logger.warn(WALLET_NOT_FOUND_LOG, fromWalletId);
                    return new RuntimeException(WALLET_NOT_FOUND);
                });
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> {
                    logger.warn(WALLET_NOT_FOUND_LOG, toWalletId);
                    return new RuntimeException(WALLET_NOT_FOUND);
                });

        if (!fromWallet.getUser().getUsername().equals(username)) {
            logger.warn(UNAUTHORIZED_ATTEMPT_LOG, "transfer", username, fromWalletId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new WalletResponse(UNAUTHORIZED));
        }
        if (!fromWallet.getCurrency().equals(toWallet.getCurrency())) {
            logger.warn("Currency mismatch for transfer from wallet {} to {}", fromWalletId, toWalletId);
            return ResponseEntity.badRequest()
                    .body(new WalletResponse(CURRENCY_MISMATCH));
        }

        try {
            fromWallet.debit(request.getAmount());
            toWallet.credit(request.getAmount());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);
            logger.info("Transferred {} {} from wallet {} to wallet {} for user {}", request.getAmount(), fromWallet.getCurrency(), fromWalletId, toWalletId, username);
            return ResponseEntity.ok(new WalletResponse(TRANSFER_SUCCESSFUL));
        } catch (IllegalStateException e) {
            logger.warn(TRANSFER_FAILED_LOG, fromWalletId, toWalletId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new WalletResponse(e.getMessage()));
        }
    }

    static class AmountRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}