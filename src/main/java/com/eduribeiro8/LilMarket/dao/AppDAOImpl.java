package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AppDAOImpl implements AppDAO{

    private final EntityManager entityManager;

    @Autowired
    public AppDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Customer customer) {
        entityManager.persist(customer);
        System.out.println("Costumer saved: " + customer);
    }

    @Override
    @Transactional
    public void save(Product product) {
        entityManager.persist(product);
        System.out.println("Product saved: " + product);
    }

    @Override
    @Transactional
    public void save(Sale sale) {
        entityManager.persist(sale);
    }

    @Override
    @Transactional
    public void save(User user) {

        TypedQuery<User> typedQuery = entityManager.createQuery("from User where userName = :username", User.class);
        typedQuery.setParameter("username", user.getUserName());

        try{
            typedQuery.getSingleResult();
        }catch (NoResultException e) {
            entityManager.persist(user);
        }
    }

    @Override
    @Transactional
    public void deleteSaleId(int id) {
        entityManager.remove(entityManager.find(Sale.class, id));
        System.out.println("Sale " + id +  " deleted!");
    }

    @Override
    public Customer findCustomerById(int id) {
        return entityManager.find(Customer.class, id);
    }

    @Override
    public Product findProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public Sale findSaleById(int id) {
        return entityManager.find(Sale.class, id);
    }
}
