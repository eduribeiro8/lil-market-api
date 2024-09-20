package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dao.SaleDAO;
import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.rest.exception.SaleNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements SaleService{

    private final SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO saleDAO) {
        this.saleDAO = saleDAO;
    }

    @Override
    @Transactional
    public Sale save(Sale sale) {
        return saleDAO.save(sale);
    }

    @Override
    public Sale findSaleById(int id) {
        Sale theSale = saleDAO.findSaleById(id);

        if (theSale == null){
            throw new SaleNotFoundException(String.valueOf(id));
        }

        return theSale;
    }
}
