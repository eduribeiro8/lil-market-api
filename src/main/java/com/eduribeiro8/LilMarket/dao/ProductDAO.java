package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Product;

import java.util.List;

public interface ProductDAO {

    Product save(Product product);

    void delete(Product product);

    List<Product> findAll();

    Product findProductByBarcode(long barcode);

    Product findProductById(int id);

    Product findProductByName(String name);
}
