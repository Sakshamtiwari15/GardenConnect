package com.gardenconnect.security;

import com.gardenconnect.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates, injects, and validates Cross-Site Request Forgery (CSRF) tokens.
 * Protects state-changing HTTP operations (POST, PUT, DELETE).
 */
public final class CsrfTokenManager {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final String CSRF_PARAM_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private CsrfTokenManager() {}

    /**
     * Generates a cryptographically secure 256-bit random token.
     */
    public static String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Obtains or creates a CSRF token associated with the user's session.
     */
    public static String getOrCreateToken(HttpSession session) {
        if (session == null) {
            return "";
        }
        String token = (String) session.getAttribute(AppConfig.SESSION_CSRF_TOKEN);
        if (token == null || token.isEmpty()) {
            token = generateToken();
            session.setAttribute(AppConfig.SESSION_CSRF_TOKEN, token);
        }
        return token;
    }

    /**
     * Validates whether the token submitted via request parameter or header matches the session token.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(AppConfig.SESSION_CSRF_TOKEN);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getHeader(CSRF_HEADER_NAME);
        }

        if (requestToken == null) {
            return false;
        }

        // Constant-time comparison to prevent timing attacks
        return constantTimeEquals(sessionToken, requestToken.trim());
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
