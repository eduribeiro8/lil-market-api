package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Sale;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SaleDAOImpl implements SaleDAO{

    private EntityManager entityManager;

    @Autowired
    public SaleDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Sale sale) {
        entityManager.persist(sale);
    }

    @Override
    public void deleteSaleId(int id) {
        entityManager.remove(findSaleById(id));
    }

    @Override
    public Sale findSaleById(int id) {
        return entityManager.find(Sale.class, id);
    }
}
