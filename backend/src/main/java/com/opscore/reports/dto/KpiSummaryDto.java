package com.opscore.reports.dto;

public record KpiSummaryDto(
        Long totalItems,
        Long approved,
        Long pending,
        Long rejected
) {}
