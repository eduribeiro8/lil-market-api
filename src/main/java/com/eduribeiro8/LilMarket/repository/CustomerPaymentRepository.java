package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    Page<CustomerPayment> findAllByCustomerIdAndPaymentDateBetween(
            Long customerId, OffsetDateTime start, OffsetDateTime end, Pageable pageable
    );
}
