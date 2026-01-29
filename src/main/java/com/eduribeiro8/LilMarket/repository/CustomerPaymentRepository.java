package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Integer> {
}
