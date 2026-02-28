package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Restock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestockRepository extends JpaRepository<Restock, Long> {

}
