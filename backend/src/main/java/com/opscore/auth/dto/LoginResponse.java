package com.opscore.auth.dto;

import com.opscore.user.Role;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        Role role,
        UUID tenantId,
        String tenantName,
        long expiresIn
) {}
