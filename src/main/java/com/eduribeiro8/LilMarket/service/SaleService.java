package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.Sale;

import java.time.OffsetDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDTO save(SaleRequestDTO saleRequestDTO);

    SaleResponseDTO findSaleById(Long id);

    List<SaleResponseDTO> getSalesByDate(OffsetDateTime start, OffsetDateTime end);

    SaleResponseDTO update(SaleRequestDTO sale);
}
