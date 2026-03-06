package com.opscore.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user doesn't have permission to perform an action.
 * Returns HTTP 403 Forbidden status.
 * 
 * Example: Insufficient role, Cross-tenant access attempt, Resource ownership violation
 */
public class ForbiddenException extends OpsCoreException {
    
    private static final String ERROR_CODE = "FORBIDDEN";
    
    public ForbiddenException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }
    
    public ForbiddenException(String resourceType, String action) {
        super(
            String.format("You don't have permission to %s %s", action, resourceType),
            ERROR_CODE,
            HttpStatus.FORBIDDEN
        );
    }
}
