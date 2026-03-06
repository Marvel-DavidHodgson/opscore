package com.opscore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for hex color codes.
 * 
 * Validates that a string is a valid hex color code:
 * - Format: #RRGGBB or #RGB
 * - Examples: #FF5733, #F57, #000000, #FFF
 * 
 * Learning points:
 * - Domain-specific validation
 * - Regular expression patterns
 * - Annotation attributes
 * 
 * Usage:
 * @ValidColor
 * private String primaryColor;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ColorValidator.class)
@Documented
public @interface ValidColor {
    
    String message() default "Color must be a valid hex code (e.g., #FF5733 or #FFF)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow short format (#RGB)
     */
    boolean allowShortFormat() default true;
}
