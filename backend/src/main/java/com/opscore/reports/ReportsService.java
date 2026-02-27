package com.opscore.reports;

import com.opscore.item.ItemRepository;
import com.opscore.item.ItemStatus;
import com.opscore.reports.dto.KpiSummaryDto;
import com.opscore.reports.dto.StatusBreakdownDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public KpiSummaryDto getKpiSummary(UUID tenantId) {
        Long totalItems = itemRepository.countByTenant_Id(tenantId);
        Long approved = itemRepository.countByTenantAndStatus(tenantId, ItemStatus.APPROVED);
        Long pending = itemRepository.countByTenantAndStatus(tenantId, ItemStatus.PENDING);
        Long rejected = itemRepository.countByTenantAndStatus(tenantId, ItemStatus.REJECTED);
        
        log.debug("KPI Summary for tenant {}: total={}, approved={}, pending={}, rejected={}", 
                  tenantId, totalItems, approved, pending, rejected);
        
        return new KpiSummaryDto(
                totalItems != null ? totalItems : 0L,
                approved != null ? approved : 0L,
                pending != null ? pending : 0L,
                rejected != null ? rejected : 0L
        );
    }

    @Transactional(readOnly = true)
    public List<StatusBreakdownDto> getStatusBreakdown(UUID tenantId) {
        List<StatusBreakdownDto> breakdown = new ArrayList<>();
        
        for (ItemStatus status : ItemStatus.values()) {
            Long count = itemRepository.countByTenantAndStatus(tenantId, status);
            breakdown.add(new StatusBreakdownDto(
                    status.name(),
                    formatStatusLabel(status),
                    count != null ? count : 0L
            ));
        }
        
        log.debug("Status breakdown for tenant {}: {} entries", tenantId, breakdown.size());
        
        return breakdown;
    }
    
    private String formatStatusLabel(ItemStatus status) {
        return switch (status) {
            case DRAFT -> "Draft";
            case ACTIVE -> "Active";
            case PENDING -> "Pending";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case CLOSED -> "Closed";
        };
    }
}
