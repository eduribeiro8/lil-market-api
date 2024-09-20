package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.rest.exception.InsufficientQuantityInSaleException;
import com.eduribeiro8.LilMarket.rest.exception.SaleNotFoundException;
import com.eduribeiro8.LilMarket.service.SaleService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

@RestController
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<Sale> getSaleById(@PathVariable int saleId){
        Sale theSale = saleService.findSaleById(saleId);
        if (theSale == null){
            throw new SaleNotFoundException();
        }
        return ResponseEntity.ok(theSale);
    }

    @PostMapping("/sale")
    public ResponseEntity<String> saveSale(@Valid @RequestBody Sale sale){
        Sale filteredSale = new Sale(sale);

        try {
            saleService.save(filteredSale);
        }catch (TransactionSystemException ex){
            throw new InsufficientQuantityInSaleException();
        }

        return ResponseEntity.ok("Sale successfully saved");
    }

    @PutMapping("/sale")
    public ResponseEntity<String> updateSale(@Valid @RequestBody Sale sale){
        Sale theSale = saleService.findSaleById(sale.getId());
        if (theSale == null){
            throw new SaleNotFoundException();
        }

        return ResponseEntity.ok("Sale successfully saved");
    }
}
