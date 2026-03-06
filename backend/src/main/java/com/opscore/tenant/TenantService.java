package com.opscore.tenant;

import com.opscore.exception.ConflictException;
import com.opscore.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "tenants", key = "#tenantId")
    public Tenant getTenantById(UUID tenantId) {
        log.debug("Fetching tenant from database: {}", tenantId);
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tenants", key = "'slug:' + #slug")
    public Tenant getTenantBySlug(String slug) {
        log.debug("Fetching tenant by slug from database: {}", slug);
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant with slug", slug));
    }

    @Transactional
    @CacheEvict(value = "tenants", key = "#tenantId")
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
    @CacheEvict(value = "tenants", key = "#tenantId")
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
            throw new ConflictException("Tenant with slug " + slug + " already exists");
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
