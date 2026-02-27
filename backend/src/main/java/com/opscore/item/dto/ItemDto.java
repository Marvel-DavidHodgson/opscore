package com.opscore.item.dto;

import com.opscore.item.ItemStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ItemDto(
        UUID id,
        UUID tenantId,
        String code,
        String title,
        String description,
        String category,
        ItemStatus status,
        UUID assignedToUserId,
        String assignedToUserName,
        UUID createdByUserId,
        String createdByUserName,
        Map<String, Object> metadata,
        Instant createdAt,
        Instant updatedAt
) {}
