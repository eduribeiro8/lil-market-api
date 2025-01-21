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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/sale/by-date")
    public ResponseEntity<List<Sale>> getSalesByDate(@RequestParam String startDate, @RequestParam String endDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date parsedStartDate = formatter.parse(startDate);
        Date parsedEndDate = formatter.parse(endDate);

        List<Sale> sales = saleService.getSalesByDate(parsedStartDate, parsedEndDate);


        return ResponseEntity.ok(sales);
    }

    @PostMapping("/sale")
    public ResponseEntity<Map<String, String>> saveSale(@Valid @RequestBody Sale sale) {
        Sale filteredSale = new Sale(sale);

        try {
            saleService.save(filteredSale);
        } catch (TransactionSystemException ex) {
            throw new InsufficientQuantityInSaleException();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sale successfully saved");
        return ResponseEntity.ok(response);
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
