package dev.dev_store_api.common.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;

public class Libs {
    private static final SecureRandom random = new SecureRandom();

    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
    }

    public static String generateOtp() {
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
