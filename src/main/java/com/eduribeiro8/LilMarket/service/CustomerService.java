package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.CustomerRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerResponseDTO;
import com.eduribeiro8.LilMarket.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<CustomerResponseDTO> findAll();

    CustomerResponseDTO findById(int id);

    CustomerResponseDTO save(CustomerRequestDTO customer);

    void deleteById(int id);
}
