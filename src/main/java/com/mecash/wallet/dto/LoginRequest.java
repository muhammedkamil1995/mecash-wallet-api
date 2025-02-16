package com.mecash.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

    @NotNull
    @NotBlank
    @Email(message = "Invalid email format")  // ✅ Ensure email is valid
    private String email;

    @NotNull
    @NotBlank(message = "Password cannot be empty")  // ✅ Prevent empty password
    private String password;

    private String role = "USER";  // ✅ Default role if not provided

    public LoginRequest() {}

    public LoginRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = (role != null && !role.isEmpty()) ? role.toUpperCase() : "USER";  // ✅ Prevent null/empty role
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = (role != null && !role.isEmpty()) ? role.toUpperCase() : "USER";  // ✅ Safe default role
    }
}
