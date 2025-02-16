package com.mecash.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class AppConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow requests from any origin (for development). Restrict in production.
        config.addAllowedOriginPattern("*"); // Use addAllowedOriginPattern instead of addAllowedOrigin
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // Allow credentials (cookies, authorization headers, etc.)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
