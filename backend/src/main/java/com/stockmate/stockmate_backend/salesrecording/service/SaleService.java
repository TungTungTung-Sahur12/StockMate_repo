package com.stockmate.stockmate_backend.salesrecording.service;

import com.stockmate.stockmate_backend.productmanagement.repository.ProductRepository;
import java.math.BigDecimal;
import com.stockmate.stockmate_backend.salesrecording.dto.CreateSaleRequest;
import com.stockmate.stockmate_backend.salesrecording.dto.SaleResponse;
import com.stockmate.stockmate_backend.salesrecording.dto.SalesSummaryResponse;
import com.stockmate.stockmate_backend.salesrecording.repository.SaleRepository;
import com.stockmate.stockmate_backend.salesrecording.validator.SaleValidator;
import com.stockmate.stockmate_backend.shared.entity.Product;
import com.stockmate.stockmate_backend.shared.entity.Sale;
import com.stockmate.stockmate_backend.shared.entity.User;
import com.stockmate.stockmate_backend.userauthentication.repository.AuthUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final AuthUserRepository userRepository;
    private final SaleValidator saleValidator;

    public SaleService(SaleRepository saleRepository,
                        ProductRepository productRepository,
                        AuthUserRepository userRepository,
                        SaleValidator saleValidator) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        saleValidator.validateCreateSaleRequest(request, product);

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Logged-in user not found"));

        java.math.BigDecimal totalAmount = product.getPrice()
                .multiply(java.math.BigDecimal.valueOf(request.getQuantitySold()));

        product.setQuantity(product.getQuantity() - request.getQuantitySold());
        productRepository.save(product);

        Sale sale = Sale.builder()
                .product(product)
                .recordedBy(currentUser)
                .quantitySold(request.getQuantitySold())
                .totalAmount(totalAmount)
                .build();
        Sale saved = saleRepository.save(sale);

        return toResponse(saved);
    }

    public List<SaleResponse> getAllSales(LocalDate startDate, LocalDate endDate, String productName) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return saleRepository.findSalesWithFilters(startDateTime, endDateTime, productName).stream()
                .map(this::toResponse)
                .toList();
    }

    public SalesSummaryResponse getSalesSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        List<Object[]> results = saleRepository.getSalesSummary(startDateTime, endDateTime);
        Object[] result = results.get(0);
        Long totalCount = ((Number) result[0]).longValue();
        BigDecimal totalRevenue = result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO;

        return new SalesSummaryResponse(totalCount, totalRevenue);
    }

    private SaleResponse toResponse(Sale sale) {
        return new SaleResponse(
                sale.getSaleId(),
                sale.getProduct().getProductId(),
                sale.getProduct().getName(),
                sale.getQuantitySold(),
                sale.getTotalAmount(),
                sale.getRecordedBy().getName(),
                sale.getCreatedAt()
        );
    }
}