package com.opscore.user.dto;

import com.opscore.user.Role;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        Role role,
        Boolean isActive
) {}
