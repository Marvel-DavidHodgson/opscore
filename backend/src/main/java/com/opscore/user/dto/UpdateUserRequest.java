package com.opscore.user.dto;

import com.opscore.user.Role;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,
        
        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,
        
        Role role,
        Boolean isActive
) {}
