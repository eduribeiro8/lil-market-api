package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByTimestampBetween(OffsetDateTime start, OffsetDateTime end);
}
