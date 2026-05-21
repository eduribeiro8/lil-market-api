package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.MovementType;
import com.eduribeiro8.LilMarket.entity.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StockMovementResponseDTO(
        Long id,
        Long productId,
        String productName,
        MovementType movementType,
        BigDecimal quantity,
        BigDecimal quantityInStock,
        Long referenceId,
        String description,
        OffsetDateTime timestamp
) {
}
