package com.opscore.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public Tenant getTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    @Transactional(readOnly = true)
    public Tenant getTenantBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    @Transactional
    public Tenant updateTenantConfig(UUID tenantId, Map<String, Object> moduleConfig, Map<String, String> labelOverrides) {
        Tenant tenant = getTenantById(tenantId);

        if (moduleConfig != null) {
            tenant.setModuleConfig(moduleConfig);
        }

        if (labelOverrides != null) {
            tenant.setLabelOverrides(labelOverrides);
        }

        Tenant updated = tenantRepository.save(tenant);
        log.info("Tenant {} configuration updated", tenant.getName());
        return updated;
    }

    @Transactional
    public Tenant updateTenantBranding(UUID tenantId, String logoUrl, String primaryColor) {
        Tenant tenant = getTenantById(tenantId);

        if (logoUrl != null) {
            tenant.setLogoUrl(logoUrl);
        }

        if (primaryColor != null) {
            tenant.setPrimaryColor(primaryColor);
        }

        Tenant updated = tenantRepository.save(tenant);
        log.info("Tenant {} branding updated", tenant.getName());
        return updated;
    }

    @Transactional
    public Tenant createTenant(String name, String slug, IndustryType industryType) {
        if (tenantRepository.existsBySlug(slug)) {
            throw new RuntimeException("Tenant with slug " + slug + " already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(name)
                .slug(slug)
                .industryType(industryType)
                .primaryColor("#1e40af")
                .isActive(true)
                .build();

        Tenant created = tenantRepository.save(tenant);
        log.info("Tenant {} created successfully", created.getName());
        return created;
    }
}
