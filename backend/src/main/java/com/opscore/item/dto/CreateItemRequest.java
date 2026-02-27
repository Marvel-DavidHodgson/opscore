package com.opscore.item.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.UUID;

public record CreateItemRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        String description,
        String category,
        UUID assignedToUserId,
        Map<String, Object> metadata
) {}
