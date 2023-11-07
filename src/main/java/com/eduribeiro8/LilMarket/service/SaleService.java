package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.entity.Sale;

public interface SaleService {

    Sale save(Sale sale);

    Sale findSaleById(int id);
}
