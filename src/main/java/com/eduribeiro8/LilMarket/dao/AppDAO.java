package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.entity.User;

public interface AppDAO {

    void save(Customer customer);

    void save(Product product);

    void save(Sale sale);

    void save(User user);

    void deleteSaleId(int id);

    Customer findCustomerById(int id);

    Product findProductById(int id);

    Sale findSaleById(int id);
}
