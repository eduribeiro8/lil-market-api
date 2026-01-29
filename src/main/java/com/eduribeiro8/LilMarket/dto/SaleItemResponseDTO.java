package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;

public record SaleItemResponseDTO(
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Integer batchId
) {}
