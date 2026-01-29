package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductCategoryRequestDTO(
        @NotBlank String name,
        @NotBlank String description
) {
}
