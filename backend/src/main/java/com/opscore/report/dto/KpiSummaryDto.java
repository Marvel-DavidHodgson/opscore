package com.opscore.report.dto;

import java.util.List;
import java.util.Map;

public record KpiSummaryDto(
        Long totalItems,
        Map<String, Long> itemsByStatus,
        Map<String, Long> itemsByAssignee,
        List<ItemStatusBreakdownDto> statusBreakdown
) {}
