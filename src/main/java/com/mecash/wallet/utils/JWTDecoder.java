package com.mecash.wallet.utils;

import java.util.Base64;

public class JWTDecoder {

    // ✅ Make this method static
    public static String decodeJWT(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT Token");
            }
            return new String(Base64.getDecoder().decode(parts[1]));
        } catch (Exception e) {
            return "Error decoding JWT: " + e.getMessage();
        }
    }
}
