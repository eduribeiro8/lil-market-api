package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SaleItemRequestDTO(
        @NotBlank @Positive Integer productId,
        @NotBlank @Positive Integer quantity
) {}
