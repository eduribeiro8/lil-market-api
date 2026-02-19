package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Requisição para reportar perda (danos/estrago) de um lote")
public record BatchLossReportRequestDTO(
        @Schema(description = "Identificador do lote", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Integer batchId,
        @Schema(description = "Quantidade perdida", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive BigDecimal quantity,
        @Schema(description = "Motivo da perda", example = "Quebrado durante transporte", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String reason
) {
}
