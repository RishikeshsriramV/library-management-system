package com.library.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2-HMAC-SHA256 with per-user random salt. Stored as base64(salt):base64(hash). */
public final class PasswordUtil {
    private static final int ITER = 120_000, BITS = 256;
    private static final SecureRandom RNG = new SecureRandom();
    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(pbkdf2(password, salt));
    }
    public static boolean verify(String password, String stored) {
        String[] p = stored.split(":");
        if (p.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(p[0]);
        byte[] expected = Base64.getDecoder().decode(p[1]);
        return MessageDigest.isEqual(expected, pbkdf2(password, salt));
    }
    private static byte[] pbkdf2(String pw, byte[] salt) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(pw.toCharArray(), salt, ITER, BITS)).getEncoded();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
