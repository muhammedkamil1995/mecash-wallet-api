package com.mecash.wallet.controller;

import com.mecash.wallet.model.Currency;
import com.mecash.wallet.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Currency> getCurrencyByCode(@PathVariable String code) {
        return ResponseEntity.ok(currencyService.getCurrencyByCode(code));
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convertCurrency(
            @RequestParam double amount,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(currencyService.convertCurrency(amount, from, to));
    }
}
