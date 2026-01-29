package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record SaleResponseDTO(
        Integer id,
        OffsetDateTime timestamp,
        String customerName,
        String sellerName,
        List<SaleItemResponseDTO> items,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal change,
        String paymentStatus,
        String notes
) {}
