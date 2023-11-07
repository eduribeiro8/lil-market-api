package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SaleController {

    private SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/sale/{saleId}")
    public Sale getSaleById(@PathVariable int saleId){
        return saleService.findSaleById(saleId);
    }


}
