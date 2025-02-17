package com.mecash.wallet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.mecash.wallet.config.JwtProperties;
import com.mecash.wallet.model.RoleType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private static final long INACTIVITY_TIMEOUT = 20L * 60 * 1000; // 20 minutes inactivity timeout

    // Constructor to initialize the JwtUtil with the JwtProperties
    public JwtUtil(JwtProperties jwtProperties) {
        // Ensure the secret is not null or empty
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isEmpty()) {
            throw new IllegalArgumentException("JWT Secret cannot be null or empty.");
        }
        
        // Generate the secret key using the secret from properties
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token
    public String generateToken(String email, RoleType role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + INACTIVITY_TIMEOUT))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Handle any exceptions during token validation (e.g., expired, malformed)
            return false;
        }
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract specific claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // Extract all claims from the token
    protected Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Refresh the token if still active
    public String refreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (System.currentTimeMillis() - claims.getIssuedAt().getTime() < INACTIVITY_TIMEOUT) {
                // Generate a new token if it's still active
                return generateToken(claims.getSubject(), RoleType.valueOf(claims.get("role", String.class)));
            }
        } catch (JwtException | IllegalArgumentException e) {
            // If the token is invalid or expired, return null
            return null;
        }
        return token; // If no refresh is needed, return the original token
    }
}
