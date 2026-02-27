package com.opscore.report.dto;

import com.opscore.item.ItemStatus;

public record ItemStatusBreakdownDto(
        ItemStatus status,
        Long count
) {}
