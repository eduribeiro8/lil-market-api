package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.CustomerPaymentResponseDTO;
import com.eduribeiro8.LilMarket.dto.CustomerRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerResponseDTO;
import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import com.eduribeiro8.LilMarket.mapper.CustomerMapper;
import com.eduribeiro8.LilMarket.mapper.CustomerPaymentMapper;
import com.eduribeiro8.LilMarket.repository.CustomerPaymentRepository;
import com.eduribeiro8.LilMarket.repository.CustomerRepository;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerPaymentRepository customerPaymentRepository;
    private  final CustomerPaymentMapper customerPaymentMapper;

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

    @Override
    public Page<CustomerPaymentResponseDTO> getCustomerTransactions(
            int id, LocalDate startDate, LocalDate endDate, Pageable pageable
    ) {
        OffsetDateTime start = startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime end = endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        Page<CustomerPayment> pages = customerPaymentRepository
                .findAllByCustomerIdAndPaymentDateBetween(id, start, end, pageable);

        return pages.map(customerPaymentMapper::toResponse);
    }
}
