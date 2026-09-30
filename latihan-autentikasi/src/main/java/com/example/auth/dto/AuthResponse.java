package com.example.auth.dto;

public class AuthResponse {
    public String accessToken;
    public String refreshToken;
    public String username;
    public String role;

    public AuthResponse(String accessToken,String refreshToken, String username, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }
}
