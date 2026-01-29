package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CustomerPaymentResponseDTO(
        Integer paymentId,
        Integer customerId,
        BigDecimal amountPaid,
        String paymentMethod,
        OffsetDateTime paymentDate,
        String notes
) {
}
