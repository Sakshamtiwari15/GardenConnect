package com.gardenconnect.exception;

/**
 * Base checked exception for the GardenConnect platform application.
 * Demonstrates Java Exception Handling hierarchy.
 */
public class GardenConnectException extends Exception {
    private static final long serialVersionUID = 1L;

    public GardenConnectException(String message) {
        super(message);
    }

    public GardenConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
