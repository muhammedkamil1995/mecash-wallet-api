package com.mecash.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // i actually disable CSRF protection for stateless API
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(java.util.List.of("*"));  // i allow all origins (it can be restricted in production)
                corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Allow required methods
                corsConfig.setAllowedHeaders(java.util.List.of("*"));  // Allow all headers
                return corsConfig;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/login", "/auth/register", "/auth/decode", "/h2-console/**").permitAll()  // Public endpoints
                .requestMatchers("/transactions/**", "/wallets/**").permitAll()  // Allow all transaction and wallet endpoints
                .anyRequest().authenticated()  // All other requests require authentication
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));  // Updated frame options

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // Allow all origins (you can customize this)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow necessary methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(false);  // Disable credentials (cookies, authorization headers)
    }
}