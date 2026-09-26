package com.adharsh.adharshmart.util;

import org.mindrot.jbcrypt.BCrypt;

/** bcrypt password hashing — mandatory engineering rule #2 (no plaintext, no MD5/SHA1). */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 10;

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean matches(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
