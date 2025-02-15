package com.mecash.wallet.exception;

public class TokenValidationResponse {
    private boolean valid;
    private String message;

    public TokenValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}
