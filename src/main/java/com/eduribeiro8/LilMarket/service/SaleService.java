package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDTO save(SaleRequestDTO saleRequestDTO);

    SaleResponseDTO findSaleById(Long id);

    Page<SaleResponseDTO> getSalesByDate(LocalDate start, LocalDate end, Pageable pageable);

    Page<SaleResponseDTO> getSalesByDateFromCustomer(LocalDate startDate, LocalDate endDate, Long customerId, Pageable pageable);

    SaleResponseDTO update(SaleRequestDTO sale);
}
