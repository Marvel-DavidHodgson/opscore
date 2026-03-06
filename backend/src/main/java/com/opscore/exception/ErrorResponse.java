package com.opscore.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response DTO returned for all exceptions.
 * Provides consistent error structure across the API.
 * 
 * Fields:
 * - timestamp: When the error occurred
 * - status: HTTP status code
 * - error: HTTP status reason phrase (e.g., "Bad Request")
 * - errorCode: Application-specific error code (e.g., "RESOURCE_NOT_FOUND")
 * - message: Human-readable error message
 * - path: Request path that caused the error
 * - requestId: Unique identifier for this request (for tracking)
 * - details: Additional error details (optional, e.g., validation errors)
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    private String requestId;
    private Map<String, String> details;
    
    /**
     * Creates a simple error response with required fields
     */
    public static ErrorResponse of(int status, String error, String errorCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .build();
    }
}
