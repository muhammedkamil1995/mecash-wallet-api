package com.mecash.wallet.service;

import com.mecash.wallet.model.Currency;
import com.mecash.wallet.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {  // Removed 'public'

    private CurrencyService currencyService;
    private CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyService = new CurrencyService(currencyRepository);

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(new Currency(1L, "USD", "US Dollar", 1.0)));
        when(currencyRepository.findByCode("NGN")).thenReturn(Optional.of(new Currency(2L, "NGN", "Naira", 760.0)));
    }

    @Test
    void testCurrencyConversion() {  // Removed 'public'
        double convertedAmount = currencyService.convertCurrency(100, "NGN", "USD");
        assertEquals(100 / 760.0, convertedAmount, 0.01);
    }
}
