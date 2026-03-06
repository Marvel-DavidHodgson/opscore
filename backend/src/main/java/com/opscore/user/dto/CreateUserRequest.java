package com.opscore.user.dto;

import com.opscore.user.Role;
import com.opscore.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @ValidPassword(minLength = 8)
        String password,
        
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,
        
        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,
        
        @NotNull(message = "Role is required")
        Role role
) {}
