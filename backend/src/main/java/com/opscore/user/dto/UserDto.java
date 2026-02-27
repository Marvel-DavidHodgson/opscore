package com.opscore.user.dto;

import com.opscore.user.Role;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        UUID tenantId,
        String email,
        String firstName,
        String lastName,
        Role role,
        Boolean isActive,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {}
