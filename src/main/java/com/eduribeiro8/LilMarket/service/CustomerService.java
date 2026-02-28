package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.*;
import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface CustomerService {

    List<CustomerResponseDTO> findAll();

    CustomerResponseDTO findById(Long id);

    CustomerResponseDTO save(CustomerRequestDTO customer);

    void deleteById(Long id);

    Page<CustomerPaymentResponseDTO> getCustomerTransactions(
            Long id, LocalDate startDate, LocalDate endDate, Pageable pageable
    );

    CustomerPaymentResponseDTO addCredit(Long id, CustomerDepositRequestDTO customerDepositRequestDTO);
}
