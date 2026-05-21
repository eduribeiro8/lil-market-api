package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementRequestDTO;
import com.eduribeiro8.LilMarket.dto.StockMovementResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementService {
    Page<StockMovementResponseDTO> getStockMovement(StockMovementRequestDTO stockMovementRequestDTO, Pageable pageable);
}
