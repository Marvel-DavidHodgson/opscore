package com.opscore.tenant;

import com.opscore.auth.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenants", description = "Tenant configuration and branding management")
@SecurityRequirement(name = "bearerAuth")
public class TenantController {

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current tenant", description = "Get configuration for the authenticated user's tenant")
    public ResponseEntity<TenantDto> getCurrentTenant(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Tenant tenant = tenantService.getTenantById(principal.tenantId());
        return ResponseEntity.ok(tenantMapper.toDto(tenant));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update current tenant", description = "Update configuration and branding for current tenant (ADMIN only)")
    public ResponseEntity<TenantDto> updateCurrentTenant(
            Authentication authentication,
            @Valid @RequestBody UpdateTenantRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Tenant tenant = tenantService.updateTenantConfig(
                principal.tenantId(),
                request.moduleConfig(),
                request.labelOverrides()
        );
        
        if (request.logoUrl() != null || request.primaryColor() != null) {
            tenant = tenantService.updateTenantBranding(
                    principal.tenantId(),
                    request.logoUrl(),
                    request.primaryColor()
            );
        }
        
        return ResponseEntity.ok(tenantMapper.toDto(tenant));
    }
}
