package com.opscore.reports;

import com.opscore.auth.JwtAuthenticationFilter;
import com.opscore.reports.dto.KpiSummaryDto;
import com.opscore.reports.dto.StatusBreakdownDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Reporting and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    private final ReportsService reportsService;

    @GetMapping("/kpis")
    @Operation(summary = "Get KPI summary", description = "Get key performance indicators")
    public ResponseEntity<KpiSummaryDto> getKpiSummary(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        KpiSummaryDto kpis = reportsService.getKpiSummary(principal.tenantId());
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/status-breakdown")
    @Operation(summary = "Get status breakdown", description = "Get item count by status")
    public ResponseEntity<List<StatusBreakdownDto>> getStatusBreakdown(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        List<StatusBreakdownDto> breakdown = reportsService.getStatusBreakdown(principal.tenantId());
        return ResponseEntity.ok(breakdown);
    }
}
