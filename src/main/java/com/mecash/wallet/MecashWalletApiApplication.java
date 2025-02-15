package com.mecash.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.mecash.wallet.repository")  // Ensure repositories are scanned
@EntityScan(basePackageClasses = {
    com.mecash.wallet.model.User.class,
    com.mecash.wallet.model.Wallet.class,
    com.mecash.wallet.model.Transaction.class,
    com.mecash.wallet.model.Currency.class
})  // Explicitly listing all entity classes
public class MecashWalletApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MecashWalletApiApplication.class, args);
    }
}
