package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.entity.Sale;

import java.util.Date;
import java.util.List;

public interface SaleService {

    Sale save(Sale sale);

    Sale findSaleById(int id);

    List<Sale> getSalesByDate(Date start, Date end);
}
