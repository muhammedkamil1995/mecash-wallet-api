package com.mecash.wallet.dto;

public class RegisterResponse {
    private String message;
    private String token;

    public RegisterResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() { return message; }
    public String getToken() { return token; }
}
