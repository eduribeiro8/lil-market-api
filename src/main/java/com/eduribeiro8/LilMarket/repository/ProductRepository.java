package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);
}
