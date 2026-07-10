package com.stockmate.stockmate_backend.productmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockmate.stockmate_backend.shared.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private ProductCategory category;
    private String size;
    private BigDecimal price;
    private Integer quantity;
    private Integer lowStockThreshold;

    @JsonProperty("isLowStock")
    private boolean isLowStock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
