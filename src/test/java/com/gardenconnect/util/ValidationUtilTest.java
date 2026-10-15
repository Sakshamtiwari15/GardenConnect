package com.gardenconnect.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationUtil Unit Tests")
class ValidationUtilTest {

    @Test
    @DisplayName("Valid emails should pass validation")
    void testValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("gardener@gardenconnect.org"));
        assertTrue(ValidationUtil.isValidEmail("sarah.green+plants@domain.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("user_123@test-mail.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@domain..com"
    })
    @DisplayName("Invalid emails should fail validation")
    void testInvalidEmails(String invalidEmail) {
        assertFalse(ValidationUtil.isValidEmail(invalidEmail));
    }

    @Test
    @DisplayName("Null email should fail validation safely")
    void testNullEmail() {
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("Valid usernames should pass validation")
    void testValidUsernames() {
        assertTrue(ValidationUtil.isValidUsername("botanist_99"));
        assertTrue(ValidationUtil.isValidUsername("sarahGreen"));
        assertTrue(ValidationUtil.isValidUsername("user123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "ab", // Too short (min 3)
            "this_username_is_way_too_long_for_the_system_limit_which_is_thirty_chars", // Too long (max 30)
            "user@name", // Disallowed special character
            "user name", // Disallowed space
            "user!name"  // Disallowed punctuation
    })
    @DisplayName("Invalid usernames should fail validation")
    void testInvalidUsernames(String invalidUsername) {
        assertFalse(ValidationUtil.isValidUsername(invalidUsername));
    }

    @Test
    @DisplayName("Strong passwords should pass validation")
    void testStrongPasswords() {
        assertTrue(ValidationUtil.isStrongPassword("Secret@123"));
        assertTrue(ValidationUtil.isStrongPassword("PlantLover#2026"));
        assertTrue(ValidationUtil.isStrongPassword("GreenGarden$8"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short1!",        // Less than 8 chars
            "alllowercase1!", // No uppercase
            "ALLUPPERCASE1!", // No lowercase
            "NoDigitsAtAll!", // No digit
            "NoSpecialChar12" // No special symbol
    })
    @DisplayName("Weak passwords should fail validation")
    void testWeakPasswords(String weakPassword) {
        assertFalse(ValidationUtil.isStrongPassword(weakPassword));
    }

    @Test
    @DisplayName("Parse positive integer with default fallback")
    void testParsePositiveInt() {
        assertEquals(5, ValidationUtil.parsePositiveInt("5", 1));
        assertEquals(1, ValidationUtil.parsePositiveInt("invalid", 1));
        assertEquals(1, ValidationUtil.parsePositiveInt("-10", 1));
        assertEquals(1, ValidationUtil.parsePositiveInt("0", 1));
        assertEquals(1, ValidationUtil.parsePositiveInt(null, 1));
    }

    @Test
    @DisplayName("Parse positive Long with default fallback")
    void testParsePositiveLong() {
        assertEquals(100L, ValidationUtil.parsePositiveLong("100", 1L));
        assertEquals(1L, ValidationUtil.parsePositiveLong("not_a_number", 1L));
        assertEquals(1L, ValidationUtil.parsePositiveLong("-50", 1L));
        assertNull(ValidationUtil.parsePositiveLong(null, null));
    }

    @Test
    @DisplayName("Safe string trims whitespace and handles null")
    void testSafeString() {
        assertEquals("Botanical Garden", ValidationUtil.safeTrim("  Botanical Garden  "));
        assertEquals("", ValidationUtil.safeTrim(null));
        assertEquals("", ValidationUtil.safeTrim("   "));
    }
}
