package com.opscore.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule validation fails.
 * Returns HTTP 400 Bad Request status.
 * 
 * Example: Invalid workflow transition, User already exists, Cannot delete active tenant
 */
public class BusinessValidationException extends OpsCoreException {
    
    private static final String ERROR_CODE = "BUSINESS_VALIDATION_FAILED";
    
    public BusinessValidationException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, cause);
    }
}
