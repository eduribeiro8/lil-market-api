package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    Page<Product> findByNameStartsWith(String startsWith, Pageable pageable);

    Page<Product> findByNameContains(String contains, Pageable pageable);

    boolean existsByBarcode(String barcode);
}
