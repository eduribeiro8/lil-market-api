package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
}
