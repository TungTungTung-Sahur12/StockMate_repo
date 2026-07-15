package com.stockmate.stockmate_backend.salesrecording.dto;

import java.math.BigDecimal;

public record SalesSummaryResponse(
        Long totalCount,
        BigDecimal totalRevenue
) {}
