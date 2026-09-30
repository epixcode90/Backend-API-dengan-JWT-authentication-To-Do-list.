#  Autentikasi — Java REST API

Backend API lengkap dengan **JWT Authentication**, **Refresh Token**, **Role-Based Access Control**, dan **CRUD Task**.

Dibangun **dari nol** tanpa framework berat seperti Spring Boot — hanya menggunakan **Javalin**, **JDBC**, dan **MySQL**.

---

## 📋 Daftar Isi

- [Fitur](#-fitur)
- [Tech Stack](#-tech-stack)
- [Arsitektur](#-arsitektur)
- [Cara Menjalankan](#-cara-menjalankan)
- [Endpoint API](#-endpoint-api)
- [Contoh Penggunaan](#-contoh-penggunaan)
- [Testing](#-testing)
- [Struktur Project](#-struktur-project)
- [Security](#-security)
- [Roadmap](#-roadmap)
- [Lisensi](#-lisensi)

---

## ✨ Fitur

### 🔑 Authentication
- ✅ Register user dengan password hashing (BCrypt)
- ✅ Login dengan JWT access token (15 menit)
- ✅ Refresh token untuk perpanjang session (7 hari)
- ✅ Logout dengan revoke refresh token
- ✅ JWT secret dari environment variable

### 👥 Authorization
- ✅ Role-based access control (USER / ADMIN)
- ✅ Middleware untuk protect endpoint
- ✅ Ownership check (user hanya akses resource sendiri)

### ✅ Task Management
- ✅ CRUD task lengkap
- ✅ Partial update (hanya field yang dikirim)
- ✅ Validasi input (title length, dll)
- ✅ Isolasi data antar user (IDOR prevention)

### 🛠️ Infrastructure
- ✅ Connection pooling dengan HikariCP
- ✅ Prepared statement (SQL injection prevention)
- ✅ Global error handling
- ✅ Unit test (JUnit 5 + Mockito)

---

## 🛠️ Tech Stack

| Komponen | Teknologi | Versi |
|----------|-----------|-------|
| **Bahasa** | Java | 17 |
| **Web Server** | Javalin | 6.3.0 |
| **Database** | MySQL | 8.4 |
| **Connection Pool** | HikariCP | 5.1.0 |
| **JSON** | Gson | 2.11.0 |
| **JWT** | JJWT | 0.12.6 |
| **Password Hash** | jBCrypt | 0.4 |
| **Testing** | JUnit 5 + Mockito | 5.10.2 / 5.11.0 |
| **Build Tool** | Maven | 3.9+ |

---

## 🏗️ Arsitektur

### Layered Architecture



### Prinsip yang Diterapkan

- **Layered Architecture** — separation of concerns
- **Repository Pattern** — abstraksi akses data
- **DTO Pattern** — kontrol data yang keluar/masuk
- **Middleware Pattern** — auth check sebelum handler
- **Singleton Pattern** — database pool
- **Dependency Injection** — constructor injection untuk testability

---

## 🚀 Cara Menjalankan

### Prerequisites

- Java 17 atau lebih baru
- MySQL 8.0 atau lebih baru
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, atau VS Code)

### 1. Clone Repository

```bash
git clone https://github.com/USERNAME/latihan-autentikasi.git
cd latihan-autentikasi
