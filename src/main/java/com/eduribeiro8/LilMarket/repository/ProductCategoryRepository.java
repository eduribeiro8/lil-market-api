package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
}
