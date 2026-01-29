package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {

    List<Batch> findByQuantityInStockGreaterThanOrderByExpirationDateAsc(int quantity);

    List<Batch> findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(int quantity, LocalDate date);

    List<Batch> findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(Product product, int quantity);

    boolean existsByBatchCode(String s);
}
