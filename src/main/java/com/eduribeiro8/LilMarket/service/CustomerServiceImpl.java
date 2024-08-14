package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dao.CustomerDAO;
import com.eduribeiro8.LilMarket.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerDAO customerDAO;

    @Autowired
    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    @Override
    public Customer findById(int id) {
        return customerDAO.findCustomerById(id);
    }

    @Override
    public Customer findByName(String name) {
        return customerDAO.findCustomerByName(name);
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        return customerDAO.update(customer);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        customerDAO.delete(id);
    }
}
