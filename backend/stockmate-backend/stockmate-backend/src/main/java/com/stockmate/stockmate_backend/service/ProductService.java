package com.stockmate.stockmate_backend.service;

import com.stockmate.stockmate_backend.dto.CreateProductRequest;
import com.stockmate.stockmate_backend.dto.ProductResponse;
import com.stockmate.stockmate_backend.entity.Product;
import com.stockmate.stockmate_backend.entity.ProductCategory;
import com.stockmate.stockmate_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .size(request.getSize())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .lowStockThreshold(
                        request.getLowStockThreshold() != null
                                ? request.getLowStockThreshold()
                                : DEFAULT_LOW_STOCK_THRESHOLD
                )
                .build();

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long productId, CreateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setSize(request.getSize());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        if (request.getLowStockThreshold() != null) {
            product.setLowStockThreshold(request.getLowStockThreshold());
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    private ProductResponse toResponse(Product product) {
        boolean isLowStock = product.getQuantity() <= product.getLowStockThreshold();

        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getSize(),
                product.getPrice(),
                product.getQuantity(),
                product.getLowStockThreshold(),
                isLowStock,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}