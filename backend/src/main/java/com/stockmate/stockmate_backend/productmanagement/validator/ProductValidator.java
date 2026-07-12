package com.stockmate.stockmate_backend.productmanagement.validator;

import com.stockmate.stockmate_backend.productmanagement.dto.CreateProductRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProductValidator {

    public void validateCreateProductRequest(CreateProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Product request is required");
        }

        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Product category is required");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (request.getLowStockThreshold() != null && request.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
    }
}
