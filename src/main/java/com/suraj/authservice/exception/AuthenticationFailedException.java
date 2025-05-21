package com.suraj.authservice.exception;

/**
 * Exception thrown when authentication process fails.
 * This exception is typically thrown when:
 * - Authentication credentials are invalid
 * - The authentication service encounters an unexpected error
 * - The authentication token is invalid or expired
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationFailedException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
