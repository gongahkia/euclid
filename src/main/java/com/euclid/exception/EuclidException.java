package com.euclid.exception;

/**
 * Base exception class for all Euclid-related exceptions.
 */
public class EuclidException extends Exception {
    /**
     * Creates a new Euclid exception.
     *
     * @param message The error message
     */
    public EuclidException(String message) {
        super(message);
    }

    /**
     * Creates a new Euclid exception with a cause.
     *
     * @param message The error message
     * @param cause   The underlying cause
     */
    public EuclidException(String message, Throwable cause) {
        super(message, cause);
    }
}
