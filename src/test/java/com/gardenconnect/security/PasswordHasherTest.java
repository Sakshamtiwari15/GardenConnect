package com.gardenconnect.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordHasher Security Tests")
class PasswordHasherTest {

    @Test
    @DisplayName("Hash generation should produce standard BCrypt format")
    void testHashPassword() {
        String plainPassword = "GardenerSecurePassword@2026";
        String hash = PasswordHasher.hashPassword(plainPassword);

        assertNotNull(hash, "BCrypt hash must not be null");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
                "BCrypt hash should start with standard cost prefix");
        assertEquals(60, hash.length(), "Standard BCrypt hashes are 60 characters long");
    }

    @Test
    @DisplayName("Verification should succeed with correct plaintext password")
    void testVerifyCorrectPassword() {
        String plainPassword = "GardenerSecurePassword@2026";
        String hash = PasswordHasher.hashPassword(plainPassword);

        assertTrue(PasswordHasher.checkPassword(plainPassword, hash),
                "Verification must return true when password matches hash");
    }

    @Test
    @DisplayName("Verification should fail with incorrect plaintext password")
    void testVerifyIncorrectPassword() {
        String plainPassword = "GardenerSecurePassword@2026";
        String wrongPassword = "WrongPassword#999";
        String hash = PasswordHasher.hashPassword(plainPassword);

        assertFalse(PasswordHasher.checkPassword(wrongPassword, hash),
                "Verification must return false when password does not match");
    }

    @Test
    @DisplayName("Verification handles null or empty inputs gracefully without throwing uncaught exceptions")
    void testNullAndEmptyInputs() {
        String hash = PasswordHasher.hashPassword("ValidPass@123");

        assertFalse(PasswordHasher.checkPassword(null, hash));
        assertFalse(PasswordHasher.checkPassword("", hash));
        assertFalse(PasswordHasher.checkPassword("ValidPass@123", null));
        assertFalse(PasswordHasher.checkPassword("ValidPass@123", ""));
        assertFalse(PasswordHasher.checkPassword("ValidPass@123", "invalid_non_bcrypt_string"));
    }
}
