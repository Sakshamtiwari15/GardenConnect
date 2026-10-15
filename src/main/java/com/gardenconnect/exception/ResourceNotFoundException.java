package com.gardenconnect.exception;

/**
 * Thrown when an entity (User, Tip, Discussion, Project, etc.) cannot be found.
 */
public class ResourceNotFoundException extends GardenConnectException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
