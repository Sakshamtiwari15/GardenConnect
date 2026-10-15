package com.gardenconnect.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thrown when user input fails business or security validation rules.
 * Demonstrates Collections (Map, HashMap) and Generics.
 */
public class ValidationException extends GardenConnectException {
    private static final long serialVersionUID = 1L;

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = (fieldErrors != null) ? new HashMap<>(fieldErrors) : new HashMap<>();
    }

    public ValidationException(String field, String errorMessage) {
        super(errorMessage);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, errorMessage);
    }

    public Map<String, String> getFieldErrors() {
        return Collections.unmodifiableMap(fieldErrors);
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}
