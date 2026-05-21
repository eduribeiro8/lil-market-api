package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.MovementType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StockMovementRequestDTO(
        @NotNull Long productId,
        MovementType movementType,
        LocalDate startDate,
        LocalDate endDate
) {
}
