package com.mecash.wallet.exception;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
