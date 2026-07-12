package com.stockmate.stockmate_backend.productmanagement.repository;

import com.stockmate.stockmate_backend.shared.entity.Product;
import com.stockmate.stockmate_backend.shared.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndNameContainingIgnoreCase(ProductCategory category, String name);
}
