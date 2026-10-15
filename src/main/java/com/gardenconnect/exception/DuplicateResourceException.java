package com.gardenconnect.exception;

/**
 * Thrown when attempting to register a unique constraint conflict
 * (e.g., duplicate username or duplicate email).
 */
public class DuplicateResourceException extends GardenConnectException {
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(String.format("%s with %s '%s' already exists in the system.", resourceName, field, value));
    }
}
