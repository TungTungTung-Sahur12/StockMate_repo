package com.stockmate.stockmate_backend.salesrecording.validator;

import com.stockmate.stockmate_backend.salesrecording.dto.CreateSaleRequest;
import com.stockmate.stockmate_backend.shared.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

    public void validateCreateSaleRequest(CreateSaleRequest request, Product product) {
        if (request == null) {
            throw new IllegalArgumentException("Sale request is required");
        }

        if (request.getQuantitySold() == null || request.getQuantitySold() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (request.getQuantitySold() > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Only " + product.getQuantity() + " unit(s) available.");
        }
    }
}