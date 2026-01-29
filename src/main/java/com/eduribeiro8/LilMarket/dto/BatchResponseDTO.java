package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BatchResponseDTO(
        Integer batchId,
        Integer productId,
        String batchCode,
        LocalDate manufactureDate,
        LocalDate expirationDate,
        Integer quantityInStock,
        Integer quantityLost,
        BigDecimal purchasePrice,
        OffsetDateTime createdAt
) {
}
