package com.mecash.wallet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;  // e.g., "USD", "NGN", "EUR"
    
    @Column(nullable = false)
    private String name;  // e.g., "US Dollar", "Naira"
    
    @Column(nullable = false)
    private Double exchangeRateToUSD;  // Rate against USD

}
