package com.gardenconnect.exception;

/**
 * Thrown when an authenticated user attempts to perform an operation
 * on a resource they do not own or do not have administrative privileges to manage.
 */
public class UnauthorizedActionException extends GardenConnectException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
