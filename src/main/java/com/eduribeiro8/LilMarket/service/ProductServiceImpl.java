package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dao.ProductDAO;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductDAO productDAO;

    @Autowired
    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Product save(Product theProduct) {
        return productDAO.save(theProduct);
    }

    @Override
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    @Override
    public Product findProductById(int productId) {
        return productDAO.findProductById(productId);
    }

}
