package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BatchLossReportRequestDTO(
        @NotNull Integer batchId,
        @Positive int quantity,
        @NotBlank String reason
) {
}
