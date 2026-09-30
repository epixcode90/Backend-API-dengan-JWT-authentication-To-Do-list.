package com.example.auth.repository;

import com.example.auth.config.Database;

import java.sql.*;
import java.util.Date;

public class RefreshTokenRepository {

    public void save(Long user_id, String token, Date expiresAt) {

        String sql = "INSERT INTO refresh_tokens (user_id,token,expires_at) VALUES (?,?,?)";

        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, user_id);
            ps.setString(2, token);
            ps.setTimestamp(3, new Timestamp(expiresAt.getTime()));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Failed to save refresh token");
            }
            System.out.println("Successfully saved refresh token" + user_id);
        } catch (SQLException e) {
            throw new RuntimeException("DB error save refresh token", e);
        }
    }


    public TokenData findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";

        try (Connection connection = Database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date expirestAt = rs.getTimestamp("expires_at");

                    if (expirestAt.before(new Date())) {
                        System.out.println("[REPO} Expired token");
                        return null;
                    }
                    return new TokenData(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("token"),
                            expirestAt
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error find refresh token", e);
        }return null;
    }


    public void deleteByToken(String token) {
        String sql = "DELETE FROM refresh_tokens WHERE token = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                System.out.println("[REPO] Token dihapus dari database");
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error saat delete token", e);
        }
    }

    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            int affected = ps.executeUpdate();

            System.out.println("[REPO] " + affected + " token dihapus untuk user " + userId);

        } catch (SQLException e) {
            throw new RuntimeException("DB error saat delete by user", e);
        }
    }

    public void deleteExpired() {
        String sql = "DELETE FROM refresh_tokens WHERE expires_at < NOW()";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int affected = ps.executeUpdate();
            if (affected > 0) {
                System.out.println("[REPO] " + affected + " token expired dihapus");
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error saat delete expired", e);
        }
    }
    public static class TokenData {
        public final Long id;
        public final Long userId;
        public final String token;
        public final Date expiresAt;

        public TokenData(Long id, Long userId, String token, Date expiresAt) {
            this.id = id;
            this.userId = userId;
            this.token = token;
            this.expiresAt = expiresAt;
        }
    }

}