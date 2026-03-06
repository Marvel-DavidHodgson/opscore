package com.opscore.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record CreateItemRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,
        
        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,
        
        @Size(max = 100, message = "Category cannot exceed 100 characters")
        String category,
        
        UUID assignedToUserId,
        Map<String, Object> metadata
) {}
