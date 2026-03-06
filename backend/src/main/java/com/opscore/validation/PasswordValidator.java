package com.opscore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for @ValidPassword annotation.
 * 
 * Learning points:
 * - ConstraintValidator interface implementation
 * - Regular expressions for pattern matching
 * - Building custom validation messages
 * - Context-aware validation
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    private int minLength;
    private boolean strictMode;
    
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.strictMode = constraintAnnotation.strictMode();
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true; // Use @NotBlank for null/empty checks
        }
        
        // Check minimum length
        if (password.length() < minLength) {
            buildCustomMessage(context, String.format("Password must be at least %d characters", minLength));
            return false;
        }
        
        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            buildCustomMessage(context, "Password must contain at least one uppercase letter");
            return false;
        }
        
        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            buildCustomMessage(context, "Password must contain at least one lowercase letter");
            return false;
        }
        
        // Check for digit
        if (!password.matches(".*\\d.*")) {
            buildCustomMessage(context, "Password must contain at least one digit");
            return false;
        }
        
        // Check for special character if strict mode
        if (strictMode && !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            buildCustomMessage(context, "Password must contain at least one special character");
            return false;
        }
        
        return true;
    }
    
    private void buildCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
