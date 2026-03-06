package com.opscore.tenant;

import com.opscore.validation.ValidColor;
import com.opscore.validation.ValidUrl;

import java.util.Map;

public record UpdateTenantRequest(
        @ValidUrl(message = "Logo URL must be a valid URL")
        String logoUrl,
        
        @ValidColor(message = "Primary color must be a valid hex code (e.g., #FF5733)")
        String primaryColor,
        
        Map<String, Object> moduleConfig,
        Map<String, String> labelOverrides
) {}
