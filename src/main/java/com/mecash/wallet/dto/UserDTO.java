package com.mecash.wallet.dto;

public class UserDTO {
    private String username;
    private String email;
    private String role;

    public UserDTO() {
    }

    public UserDTO(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public CharSequence getPassword() {
      
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }

    public void setPassword(String encode) {
      
        throw new UnsupportedOperationException("Unimplemented method 'setPassword'");
    }
}
