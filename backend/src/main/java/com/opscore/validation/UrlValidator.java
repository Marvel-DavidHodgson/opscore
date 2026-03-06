package com.opscore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator implementation for @ValidUrl annotation.
 */
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    
    private Set<String> allowedProtocols;
    private boolean requireHttps;
    
    @Override
    public void initialize(ValidUrl constraintAnnotation) {
        this.allowedProtocols = Arrays.stream(constraintAnnotation.protocols())
                .collect(Collectors.toSet());
        this.requireHttps = constraintAnnotation.requireHttps();
    }
    
    @Override
    public boolean isValid(String urlString, ConstraintValidatorContext context) {
        if (urlString == null || urlString.isEmpty()) {
            return true; // Use @NotBlank for null/empty checks
        }
        
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol().toLowerCase();
            
            // Check if protocol is allowed
            if (!allowedProtocols.contains(protocol)) {
                buildCustomMessage(context, 
                    "Protocol must be one of: " + String.join(", ", allowedProtocols));
                return false;
            }
            
            // Check HTTPS requirement
            if (requireHttps && !"https".equals(protocol)) {
                buildCustomMessage(context, "URL must use HTTPS");
                return false;
            }
            
            return true;
            
        } catch (MalformedURLException e) {
            buildCustomMessage(context, "Invalid URL format");
            return false;
        }
    }
    
    private void buildCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
