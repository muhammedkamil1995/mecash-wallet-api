package com.mecash.wallet.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role != null ? role : "USER"; // Default to "USER" if role is null
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Assuming username could be derived from email or name, implementing it:
    public String getUsername() {
        return email.split("@")[0]; // Simplistic approach, using part before '@' in email as username
    }
}