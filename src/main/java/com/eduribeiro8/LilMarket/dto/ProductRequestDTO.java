package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record ProductRequestDTO(
        @NotBlank String name,
        @NotBlank String barcode,
        String description,
        @NotNull @Positive BigDecimal price,
        @NotNull Integer categoryId,
        boolean isPerishable
) {}