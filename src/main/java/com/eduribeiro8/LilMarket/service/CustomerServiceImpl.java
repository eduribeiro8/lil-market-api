package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.CustomerRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerResponseDTO;
import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.mapper.CustomerMapper;
import com.eduribeiro8.LilMarket.repository.CustomerRepository;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponseDTO> findAll() {
        List<Customer> customers = customerRepository.findAll();

        return customerMapper.toResponseList(customers);
    }

    @Override
    public CustomerResponseDTO findById(int id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO save(CustomerRequestDTO customer) {
        Customer customerToSave = customerMapper.toEntity(customer);

        Customer savedCustomer = customerRepository.save(customerToSave);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
        customerRepository.delete(customer);
    }
}
