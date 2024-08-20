package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.SaleItem;
import com.eduribeiro8.LilMarket.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO{

    private final EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(User user) {
        if (findUserByUsername(user.getUserName()) == null) {
            entityManager.persist(user);
        }
    }

    @Override
    public User findUserById(User user) {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        TypedQuery<User> typedQuery = entityManager.createQuery("from User where userName = :username", User.class);
        typedQuery.setParameter("username", username);
        try{
            return typedQuery.getSingleResult();
        }catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean login(User user) {
        return false;
    }
}
