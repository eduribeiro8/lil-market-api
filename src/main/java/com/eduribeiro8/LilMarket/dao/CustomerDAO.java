package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;

import java.util.List;

public interface CustomerDAO {

    Customer save(Customer customer);

    Customer update(Customer customer);

    void delete(Customer customer);

    void delete(int id);

    Customer findCustomerById(int id);

    Customer findCustomerByName(String name);

    List<Customer> findAll();
}
