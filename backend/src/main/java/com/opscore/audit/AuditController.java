package com.opscore.audit;

import com.opscore.auth.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Audit", description = "Audit log endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditRepository auditRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs", description = "Get paginated audit logs for tenant (ADMIN only)")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            Authentication authentication,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID actorUserId,
            @RequestParam(required = false) Integer daysBack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (entityType != null || actorUserId != null || daysBack != null) {
            Instant startDate = daysBack != null ? 
                    Instant.now().minus(daysBack, ChronoUnit.DAYS) : 
                    Instant.now().minus(30, ChronoUnit.DAYS);
            Instant endDate = Instant.now();
            
            Page<AuditLog> logs = auditRepository.findByFilters(
                    principal.tenantId(),
                    entityType,
                    actorUserId,
                    startDate,
                    endDate,
                    pageable
            );
            return ResponseEntity.ok(logs);
        }
        
        Page<AuditLog> logs = auditRepository.findByTenant_IdOrderByCreatedAtDesc(
                principal.tenantId(),
                pageable
        );
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get entity audit trail", description = "Get audit logs for a specific entity (MANAGER+)")
    public ResponseEntity<List<AuditLog>> getEntityAuditTrail(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        
        List<AuditLog> logs = auditRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType,
                entityId
        );
        return ResponseEntity.ok(logs);
    }
}
