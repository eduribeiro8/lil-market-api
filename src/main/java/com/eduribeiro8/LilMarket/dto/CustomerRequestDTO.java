package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CustomerRequestDTO(
        @NotNull String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String address,
        @PositiveOrZero BigDecimal credit
        ) {
}
