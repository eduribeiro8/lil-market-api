package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.MovementType;
import com.eduribeiro8.LilMarket.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT m FROM StockMovement m WHERE m.product.id = :productId " +
            "AND (:movementType IS NULL OR m.movementType = :movementType) " +
            "AND (:startDate IS NULL OR m.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR m.timestamp <= :endDate)")
    Page<StockMovement> findStockMovementByProductOptionalType(@Param("productId") Long productId,
                                                               @Param("movementType") MovementType movementType,
                                                               @Param("startDate") OffsetDateTime startDate,
                                                               @Param("endDate") OffsetDateTime endDate,
                                                               Pageable pageable);

    @Query(value = """
            SELECT COALESCE(SUM(CASE WHEN movement_type = 'ENTRY' THEN quantity ELSE -quantity END), 0)
            FROM stock_movement
            WHERE product_id = :productId
              AND (timestamp < :timestamp OR (timestamp = :timestamp AND movement_id < :movementId))
            """, nativeQuery = true)
    BigDecimal sumMovementsBefore(@Param("productId") Long productId,
                                  @Param("timestamp") OffsetDateTime timestamp,
                                  @Param("movementId") Long movementId);

}
