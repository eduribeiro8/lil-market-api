package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts();

    Product findProductById(int productId);

    Product save(Product theProduct);

    Product findProductByBarcode(String productBarcode);

    Product updateProduct(Product theProduct);
}
