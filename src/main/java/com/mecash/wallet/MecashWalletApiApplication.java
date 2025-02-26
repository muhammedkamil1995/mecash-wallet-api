package com.mecash.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.mecash.wallet.repository")
public class MecashWalletApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MecashWalletApiApplication.class, args);
    }
}