package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDAOImpl implements CustomerDAO{

    private final EntityManager entityManager;

    @Autowired
    public CustomerDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        entityManager.persist(customer);
        System.out.println("Costumer saved: " + customer);
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        System.out.println("Updating customer from " + findCustomerById(customer.getId()) + " to " + customer);
        return entityManager.merge(customer);
    }

    @Override
    @Transactional
    public void delete(Customer customer) {
        entityManager.remove(customer);
    }

    @Override
    @Transactional
    public void delete(int id) {
        delete(findCustomerById(id));
    }

    @Override
    public Customer findCustomerById(int id) {
        return entityManager.find(Customer.class, id);
    }

    @Override
    public Customer findCustomerByName(String name) {
        return entityManager.find(Customer.class, name);
    }

    @Override
    public List<Customer> findAll() {
        TypedQuery<Customer> query = entityManager.createQuery("from Customer ", Customer.class);

        return query.getResultList();
    }
}
