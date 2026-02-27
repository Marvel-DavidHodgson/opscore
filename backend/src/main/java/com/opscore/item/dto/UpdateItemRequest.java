package com.opscore.item.dto;

import java.util.Map;
import java.util.UUID;

public record UpdateItemRequest(
        String title,
        String description,
        String category,
        UUID assignedToUserId,
        Map<String, Object> metadata
) {}
