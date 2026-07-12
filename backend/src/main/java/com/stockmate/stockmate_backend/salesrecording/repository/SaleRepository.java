package com.stockmate.stockmate_backend.salesrecording.repository;

import com.stockmate.stockmate_backend.shared.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOrderByCreatedAtDesc();
}