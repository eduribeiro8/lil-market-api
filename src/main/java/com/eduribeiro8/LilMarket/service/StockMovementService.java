package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementRequestDTO;
import com.eduribeiro8.LilMarket.dto.StockMovementResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface StockMovementService {
    Page<StockMovementResponseDTO> getStockMovement(StockMovementRequestDTO stockMovementRequestDTO, Pageable pageable);

    void recordEntry(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp);

    void recordExit(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp);

    void recordLoss(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp);
}
