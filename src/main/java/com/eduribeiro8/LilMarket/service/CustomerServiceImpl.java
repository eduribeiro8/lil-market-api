package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.*;
import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.entity.UserRole;
import com.eduribeiro8.LilMarket.mapper.CustomerMapper;
import com.eduribeiro8.LilMarket.mapper.CustomerPaymentMapper;
import com.eduribeiro8.LilMarket.repository.CustomerPaymentRepository;
import com.eduribeiro8.LilMarket.repository.CustomerRepository;
import com.eduribeiro8.LilMarket.rest.exception.BusinessException;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final CustomerPaymentMapper customerPaymentMapper;
    private final UserService userService;

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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public CustomerPaymentResponseDTO addCredit(int id, CustomerDepositRequestDTO customerDepositRequestDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Cliente (id = " + id + ") não encontrado!")
        );

        User user = userService.findById(customerDepositRequestDTO.userId());

        if (user.getRole().equals(UserRole.ROLE_USER)) throw new BusinessException("Usuário não pode fazer esta ação.");

        customer.addCredit(customerDepositRequestDTO.customerPaymentRequestDTO().amountPaid());

        CustomerPayment customerPayment = customerPaymentMapper.toEntity(customerDepositRequestDTO.customerPaymentRequestDTO());

        customerPayment.setCustomer(customer);

        customerPayment.setNotes(
                "Crédito adicionado pelo usuario " + user.getFirstName() + "(id = " + user.getId() + ")."
        );

        customerPaymentRepository.save(customerPayment);


        return customerPaymentMapper.toResponse(customerPayment);
    }
}
