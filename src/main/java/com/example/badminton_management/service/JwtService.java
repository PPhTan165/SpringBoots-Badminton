package com.example.badminton_management.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${api.secret.key}")
    private String secretKey;

    private final long accessTokenExpire = 1000L * 60 * 15; // 15 minutes
    private final long refreshTokenExpire = 1000L * 60 * 60 * 24 * 7; // 7 days

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpire, "ACCESS");
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpire, "REFRESH");
    }

    public String generateToken(String username, long expireTime, String type) {
        return Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isRefreshTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        String type = extractClaim(token, claims -> claims.get("type", String.class));

        return extractedUsername.equals(username)
                && "REFRESH".equals(type)
                && !isTokenExpire(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpire(token);
    }

    private boolean isTokenExpire(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secretKey.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
