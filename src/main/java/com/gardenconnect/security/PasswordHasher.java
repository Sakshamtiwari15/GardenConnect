package com.gardenconnect.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles cryptographic password hashing and verification using BCrypt.
 * Applies a work factor of 12 for strong brute-force resistance.
 */
public final class PasswordHasher {

    private static final int BCRYPT_WORK_FACTOR = 12;

    private PasswordHasher() {}

    /**
     * Hashes a plaintext password with a fresh salt.
     *
     * @param plainTextPassword The raw password provided by user.
     * @return The BCrypt hash string.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     *
     * @param plainTextPassword The raw candidate password.
     * @param storedHash The stored hashed password.
     * @return true if password matches hash, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // Malformed hash format
            return false;
        }
    }
}
