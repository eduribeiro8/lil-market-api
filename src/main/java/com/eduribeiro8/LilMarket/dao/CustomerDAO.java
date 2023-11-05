package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;

public interface CustomerDAO {

    void save(Customer customer);

    void delete(Customer customer);

    Customer findCustomerById(int id);

    Customer findCustomerByName(String name);
}
