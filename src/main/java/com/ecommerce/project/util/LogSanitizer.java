package com.ecommerce.project.util;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing log messages to prevent sensitive data exposure.
 * 
 * This class provides methods to mask or remove sensitive information such as:
 * - SMTP passwords and credentials
 * - API keys and secrets
 * - Email passwords
 * - Connection strings with embedded credentials
 * 
 * @see Requirements 10.3 - Sensitive data not logged
 */
public final class LogSanitizer {

    private LogSanitizer() {
        // Utility class - prevent instantiation
    }

    // Patterns for sensitive data detection
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(password|passwd|pwd|secret|credential|auth)\\s*[=:]\\s*['\"]?([^'\"\\s,;]+)['\"]?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern API_KEY_PATTERN = Pattern.compile(
            "(api[_-]?key|apikey|key[_-]?id|access[_-]?key)\\s*[=:]\\s*['\"]?([^'\"\\s,;]+)['\"]?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SMTP_AUTH_PATTERN = Pattern.compile(
            "(smtp|mail)\\.(password|auth|user)\\s*[=:]\\s*['\"]?([^'\"\\s,;]+)['\"]?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CONNECTION_STRING_PATTERN = Pattern.compile(
            "://([^:]+):([^@]+)@",
            Pattern.CASE_INSENSITIVE
    );

    private static final String MASKED_VALUE = "[REDACTED]";

    /**
     * Sanitizes a message by masking any sensitive data patterns.
     * 
     * @param message the message to sanitize
     * @return the sanitized message with sensitive data masked
     */
    public static String sanitize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        String sanitized = message;
        
        // Mask password patterns
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1=" + MASKED_VALUE);
        
        // Mask API key patterns
        sanitized = API_KEY_PATTERN.matcher(sanitized).replaceAll("$1=" + MASKED_VALUE);
        
        // Mask SMTP auth patterns
        sanitized = SMTP_AUTH_PATTERN.matcher(sanitized).replaceAll("$1.$2=" + MASKED_VALUE);
        
        // Mask credentials in connection strings (e.g., mongodb://user:pass@host)
        sanitized = CONNECTION_STRING_PATTERN.matcher(sanitized).replaceAll("://$1:" + MASKED_VALUE + "@");

        return sanitized;
    }

    /**
     * Sanitizes an exception message and its cause chain.
     * 
     * @param throwable the exception to sanitize
     * @return a sanitized error message string
     */
    public static String sanitizeException(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getSimpleName());
        
        String message = throwable.getMessage();
        if (message != null && !message.isEmpty()) {
            sb.append(": ").append(sanitize(message));
        }

        // Include cause if present (but sanitized)
        Throwable cause = throwable.getCause();
        if (cause != null && cause != throwable) {
            sb.append(" | Caused by: ").append(sanitizeException(cause));
        }

        return sb.toString();
    }

    /**
     * Checks if a string contains any sensitive data patterns.
     * Useful for validation before logging.
     * 
     * @param text the text to check
     * @return true if sensitive data patterns are detected
     */
    public static boolean containsSensitiveData(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(text).find() ||
               API_KEY_PATTERN.matcher(text).find() ||
               SMTP_AUTH_PATTERN.matcher(text).find() ||
               CONNECTION_STRING_PATTERN.matcher(text).find();
    }
}
