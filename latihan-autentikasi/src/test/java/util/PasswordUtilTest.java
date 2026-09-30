package util;

import com.example.auth.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("hash password menghasilkan hash yang valid")
    void testhashprodusesvalidBcryt(){
        String password = "123456ayam";
        String hashedPassword = PasswordUtil.hash(password);

        assertNotNull(hashedPassword,"hash tidak boleh null");
        assertFalse(hashedPassword.isEmpty(),"hash tidak boleh kosong");
        assertTrue(hashedPassword.startsWith("$2a$"),"hash harus dimulai dengan $2a$");
        assertTrue(hashedPassword.length() >=60,"hash harus minimal 60 karakter");
    }

    @Test
    @DisplayName("test cek password yang benar sama dengan hash")
    void testcekpasswordyangbenarsama(){
        String password = "123456ayam";
        String hashedPassword = PasswordUtil.hash(password);

        boolean hasil = PasswordUtil.check(password,hashedPassword);

        assertTrue(hasil,"hash harus sama ");
    }

    @Test
    @DisplayName("test cek passord yang salah bebeda dengan hash")
    void testcekpassordyangsalahbebeda(){
        String password = "123456ayam";
        String passwordSalah = "udain1231";
        String hashedPassword = PasswordUtil.hash(password);
        boolean hasil = PasswordUtil.check(passwordSalah,hashedPassword);

        assertFalse(hasil,"hash harus berbeda ");
    }

    @Test
    @DisplayName("Menhash password ke 2 kali meng hasilkan hash yang berbeda")
    void testMenhashpasswordke2kali(){
        String password = "123456ayam";

        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);

        assertNotEquals(hash1,hash2,"hash 1 dan 2 bebeda");

        assertTrue(PasswordUtil.check(password,hash1),"harus sama");
        assertTrue(PasswordUtil.check(password,hash2),"harus sama");
    }

    @Test
    @DisplayName("Test cek hash yang berbeda idak sama dengan password")
    void testcekhashyangberbedaidak(){
        String password = "123456ayam";
        String hashSala ="1231asdaag";

        boolean hasil = PasswordUtil.check(password,hashSala);

        assertFalse(hasil,"hash yang berbeda ");
    }
}
