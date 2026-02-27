package com.opscore.user.dto;

import com.opscore.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        String password,
        
        String firstName,
        String lastName,
        
        @NotNull(message = "Role is required")
        Role role
) {}
