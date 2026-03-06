package com.opscore.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails or token is invalid/expired.
 * Returns HTTP 401 Unauthorized status.
 * 
 * Example: Invalid credentials, Expired token, Missing authentication
 */
public class UnauthorizedException extends OpsCoreException {
    
    private static final String ERROR_CODE = "UNAUTHORIZED";
    
    public UnauthorizedException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }
}
