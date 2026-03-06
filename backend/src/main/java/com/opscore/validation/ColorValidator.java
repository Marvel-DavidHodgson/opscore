package com.opscore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for @ValidColor annotation.
 * 
 * Validates hex color codes in both long (#RRGGBB) and short (#RGB) formats.
 */
public class ColorValidator implements ConstraintValidator<ValidColor, String> {
    
    private static final Pattern HEX_COLOR_LONG = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Pattern HEX_COLOR_SHORT = Pattern.compile("^#[0-9A-Fa-f]{3}$");
    
    private boolean allowShortFormat;
    
    @Override
    public void initialize(ValidColor constraintAnnotation) {
        this.allowShortFormat = constraintAnnotation.allowShortFormat();
    }
    
    @Override
    public boolean isValid(String color, ConstraintValidatorContext context) {
        if (color == null || color.isEmpty()) {
            return true; // Use @NotBlank for null/empty checks
        }
        
        // Check long format (#RRGGBB)
        if (HEX_COLOR_LONG.matcher(color).matches()) {
            return true;
        }
        
        // Check short format (#RGB) if allowed
        if (allowShortFormat && HEX_COLOR_SHORT.matcher(color).matches()) {
            return true;
        }
        
        return false;
    }
}
