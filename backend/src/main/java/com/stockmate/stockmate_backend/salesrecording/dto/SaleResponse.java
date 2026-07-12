package com.stockmate.stockmate_backend.salesrecording.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long saleId,
        Long productId,
        String productName,
        Integer quantitySold,
        BigDecimal totalAmount,
        String recordedByName,
        LocalDateTime createdAt
) {}