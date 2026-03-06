package com.opscore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for URL fields.
 * 
 * Validates that a string is a valid URL.
 * Supports HTTP, HTTPS, and optionally other protocols.
 * 
 * Usage:
 * @ValidUrl
 * private String logoUrl;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UrlValidator.class)
@Documented
public @interface ValidUrl {
    
    String message() default "Must be a valid URL";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allowed protocols (default: http, https)
     */
    String[] protocols() default {"http", "https"};
    
    /**
     * Require HTTPS
     */
    boolean requireHttps() default false;
}
