package com.opscore.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an operation conflicts with the current state.
 * Returns HTTP 409 Conflict status.
 * 
 * Example: Duplicate email, Resource already exists, Concurrent modification
 */
public class ConflictException extends OpsCoreException {
    
    private static final String ERROR_CODE = "CONFLICT";
    
    public ConflictException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT);
    }
}
