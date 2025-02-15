package com.mecash.wallet.service;

import com.mecash.wallet.exception.CurrencyNotFoundException;
import com.mecash.wallet.model.Currency;
import com.mecash.wallet.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code " + code + " not found."));
    }

    public double convertCurrency(double amount, String fromCode, String toCode) {
        Currency fromCurrency = getCurrencyByCode(fromCode);
        Currency toCurrency = getCurrencyByCode(toCode);

        // Convert to USD first, then to target currency
        double amountInUSD = amount / fromCurrency.getExchangeRateToUSD();
        return amountInUSD * toCurrency.getExchangeRateToUSD();
    }
}
