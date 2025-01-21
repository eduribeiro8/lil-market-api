package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.entity.SaleItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public class SaleDAOImpl implements SaleDAO{

    private final EntityManager entityManager;

    @Autowired
    public SaleDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Sale save(Sale sale) {
        Customer customer = entityManager.find(Customer.class, sale.getCustomer().getId());
        customer.addDebt(sale.getTotal());

        for (SaleItem saleItem: sale.getItems()){
            Product product = entityManager.find(Product.class, saleItem.getProduct().getId());
            product.decreaseQuantity(saleItem.getQuantity());
            entityManager.persist(product);
        }


        entityManager.persist(sale);
        entityManager.persist(customer);
        return sale;
    }

    @Override
    public void deleteSaleId(int id) {
        entityManager.remove(findSaleById(id));
    }

    @Override
    public Sale findSaleById(int id) {
        return entityManager.find(Sale.class, id);
    }

    @Override
    public List<Sale> findSalesByDate(Date startDate, Date endDate) {
        TypedQuery<Sale> typedQuery = entityManager.createQuery("select new Sale(s.id, s.customer, s.timestamp, s.total) from Sale s where s.timestamp between :startDate and :endDate", Sale.class);

        typedQuery.setParameter("startDate", startDate);
        typedQuery.setParameter("endDate", endDate);

        try{
            return typedQuery.getResultList();
        } catch (NoResultException e) {
            System.out.println("No sales in DB");
        }
        return null;
    }
}
