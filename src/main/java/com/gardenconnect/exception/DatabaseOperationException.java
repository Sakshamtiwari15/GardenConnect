package com.gardenconnect.exception;

/**
 * Thrown when an underlying JDBC or database transaction error occurs.
 * Protects higher layers from raw SQL details while logging internal causes.
 */
public class DatabaseOperationException extends GardenConnectException {
    private static final long serialVersionUID = 1L;

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
