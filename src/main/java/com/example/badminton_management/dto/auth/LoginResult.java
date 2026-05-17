package com.example.badminton_management.dto.auth;

public class LoginResult {
    private String message;
    private String accessToken;
    private String refreshToken;

    public LoginResult(String message, String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken(){return refreshToken;}
}
