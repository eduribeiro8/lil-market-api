package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Product;

public interface ProductDAO {

    void save(Product product);

    void delete(Product product);

    Product findProductById(int id);

    Product findProductByName(String name);
}
