package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDAOImpl implements ProductDAO{

    private final EntityManager entityManager;

    @Autowired
    public ProductDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Product save(Product product) {
        entityManager.persist(product);
        return product;
    }

    @Override
    @Transactional
    public void delete(Product product) {
        entityManager.remove(product);
    }

    @Override
    public Product findProductByBarcode(long barcode){
        System.out.println("DAO -> Searching product which barcode = " + barcode);

        TypedQuery<Product> typedQuery = entityManager.createQuery("from Product where barcode = :barcode", Product.class);
        typedQuery.setParameter("barcode", barcode);

        try {
            return typedQuery.getSingleResult();
        }catch (NoResultException e){
            System.out.println("ERROR -> Product not found in fun findProductById where barcode = " + barcode);
        }
        return null;
    }

    @Override
    public Product findProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> typedQuery = entityManager.createQuery("from Product ", Product.class);
        return typedQuery.getResultList();
    }

    @Override
    public Product findProductByName(String name) {
        return entityManager.find(Product.class, name);
    }

    @Override
    @Transactional
    public Product updateProduct(Product theProduct) {
        Product oldProduct = entityManager.find(Product.class, theProduct.getId());
        oldProduct.updateProduct(theProduct);

        return entityManager.merge(oldProduct);
    }
}
