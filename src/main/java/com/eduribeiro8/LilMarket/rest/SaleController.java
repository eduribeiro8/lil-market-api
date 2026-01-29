package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;


    @GetMapping("/sale/{saleId}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable int saleId){
        return ResponseEntity.ok(saleService.findSaleById(saleId));
    }

    @GetMapping("/sale/by-date")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        return ResponseEntity.ok(saleService.getSalesByDate(start, end));
    }

    @PostMapping("/sale")
    public ResponseEntity<SaleResponseDTO> saveSale(@Valid @RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO savedSale = saleService.save(saleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
    }


}
