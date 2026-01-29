package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;

public record BatchInvalidationRequestDTO(
        @NotBlank String reason
) {}
