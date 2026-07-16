package com.stockmate.stockmate_backend.salesrecording.repository;

import com.stockmate.stockmate_backend.shared.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("""
            SELECT s FROM Sale s
            JOIN s.product p
            WHERE (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
              AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
              AND (:productName IS NULL OR :productName = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%')))
            ORDER BY s.createdAt DESC
            """)
    List<Sale> findSalesWithFilters(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("productName") String productName);

    @Query("""
            SELECT COUNT(s), COALESCE(SUM(s.totalAmount), 0)
            FROM Sale s
            WHERE (CAST(:startDate AS timestamp) IS NULL OR s.createdAt >= :startDate)
              AND (CAST(:endDate AS timestamp) IS NULL OR s.createdAt <= :endDate)
            """)
    List<Object[]> getSalesSummary(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
}