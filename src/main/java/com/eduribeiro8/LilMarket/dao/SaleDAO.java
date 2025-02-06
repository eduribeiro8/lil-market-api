package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Sale;

import java.util.Date;
import java.util.List;

public interface SaleDAO {

    Sale save(Sale sale);

    void deleteSaleId(int id);

    Sale findSaleById(int id);

    List<Sale> findSalesByDate(Date startDate, Date endDate);

    Sale update(Sale sale);
}
