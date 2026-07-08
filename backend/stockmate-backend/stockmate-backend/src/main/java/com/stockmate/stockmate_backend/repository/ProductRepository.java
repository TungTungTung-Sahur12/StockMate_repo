package com.stockmate.stockmate_backend.repository;

import com.stockmate.stockmate_backend.entity.Product;
import com.stockmate.stockmate_backend.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
}