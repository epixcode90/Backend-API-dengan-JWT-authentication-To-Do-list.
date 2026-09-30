package com.example.auth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Database {


    private static HikariDataSource dataSource;


    public static void init() {

        try {

            Properties properties = new Properties();

            try (InputStream in = Database.class
                    .getClassLoader()
                    .getResourceAsStream("hikari.properties")) {
                if (in == null) {
                    throw new NullPointerException("hikari.properties resource not found!");
                }
                properties.load(in);
            }

            HikariConfig config = new HikariConfig(properties);
            dataSource = new HikariDataSource(config);

            createTables();

        } catch (IOException e) {
            throw new RuntimeException("gagal baca hikari propertis ", e);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal init database", e);
        }

    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("dataSource not initialized");
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Pool ditutup.");
        }
    }

    private static void createTables() throws SQLException {
        String creatTableUser = """
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        role VARCHAR(20) NOT NULL DEFAULT 'USER',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        INDEX idx_username (username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

        String createRefreshTokens = """
                    CREATE TABLE IF NOT EXISTS refresh_tokens (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        token VARCHAR(500) UNIQUE NOT NULL,
                        expires_at TIMESTAMP NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        INDEX idx_token (token),
                        INDEX idx_user_id (user_id),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

        String createTasks = """
                    CREATE TABLE IF NOT EXISTS tasks (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        title VARCHAR(200) NOT NULL,
                        description TEXT,
                        completed BOOLEAN NOT NULL DEFAULT FALSE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP 
                                     ON UPDATE CURRENT_TIMESTAMP,
                        INDEX idx_user_id (user_id),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(creatTableUser);
            System.out.println("[DB] Tabel users siap.");

            stmt.execute(createRefreshTokens);
            System.out.println("[DB] Tabel refres token siap.");

            stmt.execute(createTasks);
            System.out.println("[DB] Tabel Tasks siap.");
        }
    }
}
