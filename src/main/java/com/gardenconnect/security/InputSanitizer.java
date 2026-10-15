package com.gardenconnect.security;

/**
 * Sanitizes untrusted user text to mitigate Cross-Site Scripting (XSS) attacks.
 */
public final class InputSanitizer {

    private InputSanitizer() {}

    /**
     * Escapes standard HTML special characters: &, <, >, ", '
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder out = new StringBuilder(Math.max(16, input.length()));
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '"':
                    out.append("&quot;");
                    break;
                case '\'':
                    out.append("&#x27;");
                    break;
                case '/':
                    out.append("&#x2F;");
                    break;
                default:
                    out.append(c);
                    break;
            }
        }
        return out.toString();
    }

    /**
     * Strips potentially dangerous script tags or malformed code.
     */
    public static String stripMaliciousTags(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)<script.*?>.*?</script.*?>", "")
                .replaceAll("(?i)<iframe.*?>.*?</iframe.*?>", "")
                .replaceAll("(?i)javascript:", "")
                .replaceAll("(?i)onload=", "")
                .replaceAll("(?i)onerror=", "");
    }
}
