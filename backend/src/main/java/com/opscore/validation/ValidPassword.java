package com.opscore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for password strength.
 * 
 * Requirements:
 * - At least 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character (optional based on strictMode)
 * 
 * Learning points:
 * - Creating custom constraint annotations
 * - Constraint validation API (JSR-303/380)
 * - Annotation processing
 * - Reusable validation logic
 * 
 * Usage:
 * @ValidPassword
 * private String password;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {
    
    String message() default "Password must be at least 8 characters with uppercase, lowercase, and digit";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Require special characters
     */
    boolean strictMode() default false;
    
    /**
     * Minimum length
     */
    int minLength() default 8;
}
