package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.PaymentMethod;
import com.eduribeiro8.LilMarket.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequestDTO(
        @NotNull Integer customerId,
        @NotNull Long userId,
        @NotNull List<SaleItemRequestDTO> items,
        @NotNull BigDecimal amountPaid,
        @NotNull Boolean isOnAccount,
        String notes,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod
) {}
