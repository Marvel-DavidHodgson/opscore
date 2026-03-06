package com.opscore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all OpsCore business exceptions.
 * Extends RuntimeException to avoid checked exception handling throughout the codebase.
 * 
 * All custom exceptions should extend this class to ensure consistent error handling.
 */
@Getter
public abstract class OpsCoreException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    protected OpsCoreException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    protected OpsCoreException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
