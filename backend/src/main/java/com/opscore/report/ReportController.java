package com.opscore.report;

import com.opscore.auth.JwtAuthenticationFilter;
import com.opscore.item.Item;
import com.opscore.item.ItemStatus;
import com.opscore.report.dto.KpiSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Reporting and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/kpi")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get KPI summary", description = "Get key performance indicators (MANAGER+)")
    public ResponseEntity<KpiSummaryDto> getKpiSummary(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        KpiSummaryDto kpi = reportService.getKpiSummary(principal.tenantId());
        return ResponseEntity.ok(kpi);
    }

    @GetMapping("/items/export")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Export items to CSV", description = "Export filtered items as CSV (MANAGER+)")
    public ResponseEntity<byte[]> exportItems(
            Authentication authentication,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String category) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        List<Item> items = reportService.getItemsForExport(principal.tenantId(), status, category);
        
        byte[] csv = generateCsv(items);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "items-export.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get items by category", description = "Get item counts grouped by category (MANAGER+)")
    public ResponseEntity<Map<String, Long>> getItemsByCategory(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Map<String, Long> categoryCounts = reportService.getItemCountsByCategory(principal.tenantId());
        return ResponseEntity.ok(categoryCounts);
    }

    private byte[] generateCsv(List<Item> items) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos, false, StandardCharsets.UTF_8)) {
            
            // CSV Header
            writer.println("Code,Title,Description,Category,Status,Created At,Updated At");
            
            // CSV Rows
            for (Item item : items) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                        escapeCsv(item.getCode()),
                        escapeCsv(item.getTitle()),
                        escapeCsv(item.getDescription()),
                        escapeCsv(item.getCategory()),
                        item.getStatus(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()
                );
            }
            
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating CSV", e);
            throw new RuntimeException("Failed to generate CSV export", e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
