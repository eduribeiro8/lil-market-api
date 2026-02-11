package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Supplier findByName(String name);
}
