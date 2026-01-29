package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchRequestDTO(
        @NotNull Integer productId,
        String batchCode,
        LocalDate manufactureDate,
        @NotNull LocalDate expirationDate,
        @NotNull @PositiveOrZero Integer quantityInStock,
        @PositiveOrZero Integer quantityLost,
        @NotNull @Positive BigDecimal purchasePrice
        ) {
}
