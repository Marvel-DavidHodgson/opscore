package com.opscore.tenant;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TenantDto(
        UUID id,
        String name,
        String slug,
        String logoUrl,
        String primaryColor,
        IndustryType industryType,
        Map<String, Object> moduleConfig,
        Map<String, String> labelOverrides,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
