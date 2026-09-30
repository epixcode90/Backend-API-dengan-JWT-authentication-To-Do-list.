package com.example.auth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int COST=12;

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }

    public static boolean check(String password, String hash) {
        try {
            return BCrypt.checkpw(password, hash);
        }catch (Exception e) {
            return false;
        }
    }
}
