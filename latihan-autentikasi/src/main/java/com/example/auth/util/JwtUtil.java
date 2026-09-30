package com.example.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = getSecret();
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long ACCESS_EXPIRATION_TIME = 15 * 60 * 1000;
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    private static String getSecret() {
        // 1. Coba baca dari ENV
        String envSecret = System.getenv("JWT_SECRET");
        if (envSecret != null && envSecret.length() >= 32) {
            System.out.println("[JWT] ✅ Menggunakan secret dari ENV");
            return envSecret;
        }

        // 2. Fallback (development only)
        System.out.println("[JWT] ⚠️ Development mode: pakai secret default");
        System.out.println("[JWT]    Di production, set ENV JWT_SECRET!");
        return "dev-only-fallback-secret-minimal-32-karakter-oke";
    }

    public static String generateAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String generateRefreshToken(Long userId, String username) {

        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("userId",userId)
                .claim("type","refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime()+REFRESH_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();

    }
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public static Date getRefreshTokenExpiration() {
        return new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME);
    }
}
