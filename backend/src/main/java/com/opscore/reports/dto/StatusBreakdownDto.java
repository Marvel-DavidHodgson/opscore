package com.opscore.reports.dto;

public record StatusBreakdownDto(
        String status,
        String label,
        Long count
) {}
