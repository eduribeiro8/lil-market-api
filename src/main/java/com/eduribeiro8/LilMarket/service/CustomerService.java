package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findById(int id);

    Customer findByName(String name);

    Customer save(Customer customer);

    void deleteById(int id);
}
