package com.example.badminton_management.config;

import com.example.badminton_management.dto.auth.LoginResult;
import com.example.badminton_management.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;

    public OAuth2LoginSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException{
        int expireRefreshToken = 7*24*60*60;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        LoginResult result = authService.loginWithGoogle(oAuth2User);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(expireRefreshToken)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect(
                "http://localhost:3000/oauth2/success?accessToken=" +
                        URLEncoder.encode(result.getAccessToken(), StandardCharsets.UTF_8)
        );
    }
}
