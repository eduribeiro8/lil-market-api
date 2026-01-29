package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CustomerPaymentRequestDTO(
        Integer customerId,
        @NotNull @PositiveOrZero BigDecimal amountPaid,
        @NotBlank PaymentMethod paymentMethod,
        String notes
) {
}
