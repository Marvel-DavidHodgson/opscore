package com.opscore.tenant;

import java.util.Map;

public record UpdateTenantRequest(
        String logoUrl,
        String primaryColor,
        Map<String, Object> moduleConfig,
        Map<String, String> labelOverrides
) {}
