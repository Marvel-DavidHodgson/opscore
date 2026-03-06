package com.opscore.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource cannot be found in the database.
 * Returns HTTP 404 Not Found status.
 * 
 * Example: Item with ID x not found, User not found, Tenant not found
 */
public class ResourceNotFoundException extends OpsCoreException {
    
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    
    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(
            String.format("%s with identifier '%s' not found", resourceType, identifier),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
    
    public ResourceNotFoundException(String message) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }
}
