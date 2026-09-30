package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.PasswordUtil;

import java.util.Date;

public class AuthService {

    private final UserRepository repo = new UserRepository();
    private final RefreshTokenRepository refreshRepo = new RefreshTokenRepository();

    public User register(RegisterRequest req) {

        if (req.username == null || req.username.isEmpty()) {
            throw new IllegalArgumentException("Username is empty");
        }

        if (req.username.length() < 3) {
            throw new IllegalArgumentException("Username length is less than 3 characters");
        }

        if (req.password == null || req.password.isEmpty()) {
            throw new IllegalArgumentException("Password is empty");
        }

        if (req.password.length() < 6) {
            throw new IllegalArgumentException("Password length is less than 3 characters");
        }

        if (repo.findByUsername(req.username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(req.username);
        user.setPassword(PasswordUtil.hash(req.password));
        user.setRole("USER");

        return repo.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        if (req.username == null || req.password == null) {
            throw new IllegalArgumentException("Username is empty");
        }

        User user = repo.findByUsername(req.username);
        if (user == null || !PasswordUtil.check(req.password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String accessToken = JwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        Date expiration = JwtUtil.getRefreshTokenExpiration();
        refreshRepo.save(user.getId(), refreshToken, expiration);

        System.out.println("[AUTH] login successful" + user.getUsername() + "(refresh token disimpan");

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getRole());
    }

    public AuthResponse refresh(RefreshRequest req) {
        // Validasi input
        if (req.refreshToken == null || req.refreshToken.isBlank())
            throw new IllegalArgumentException("Refresh token wajib diisi");

        // Cek di database — apakah token valid & belum expired?
        RefreshTokenRepository.TokenData tokenData = refreshRepo.findByToken(req.refreshToken);

        if (tokenData == null) {
            throw new IllegalArgumentException("Refresh token tidak valid atau sudah expired");
        }

        // Cari user (untuk dapat data terbaru — misal role sudah berubah)
        User user = repo.findById(tokenData.userId);

        if (user == null) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }

        // Generate access token BARU
        String newAccessToken = JwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

        System.out.println("[AUTH] Refresh sukses untuk user: " + user.getUsername());

        // Return access token baru + refresh token yang sama
        return new AuthResponse(newAccessToken, req.refreshToken,          // refresh token tidak berubah
                user.getUsername(), user.getRole());
    }


    public void logout(RefreshRequest req) {
        if (req.refreshToken == null || req.refreshToken.isBlank())
            throw new IllegalArgumentException("Refresh token wajib diisi");

        refreshRepo.deleteByToken(req.refreshToken);

        System.out.println("[AUTH] Logout sukses (refresh token dihapus)");
    }

}
