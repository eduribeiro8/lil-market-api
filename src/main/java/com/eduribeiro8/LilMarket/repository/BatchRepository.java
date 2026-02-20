package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {

    List<Batch> findByQuantityInStockGreaterThanOrderByExpirationDateAsc(BigDecimal quantity);

    List<Batch> findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(BigDecimal quantity, LocalDate date);

    Page<Batch> findByQuantityInStockGreaterThanAndExpirationDateBetween(
            BigDecimal quantity, LocalDate startDate, LocalDate endDate, Pageable pageable
    );

    List<Batch> findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(Product product, BigDecimal quantity);

    List<Batch> findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(Product product, BigDecimal quantity);

    boolean existsByBatchCode(String s);

    Page<Batch> findByQuantityInStockGreaterThan(BigDecimal i, Pageable pageable);

    Page<Batch> findByRestockId(Integer restockId, Pageable pageable);

    @Query("SELECT SUM(b.quantityInStock * b.purchasePrice) / SUM(b.quantityInStock) " +
            "FROM Batch b " +
            "WHERE b.product.id = :productId AND b.quantityInStock > 0")
    BigDecimal calculateAverageCostByProduct(@Param("productId") Integer productId);
}
