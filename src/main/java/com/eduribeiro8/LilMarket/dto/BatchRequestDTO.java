package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para criar/atualizar lote")
public record BatchRequestDTO(
        @Schema(description = "Identificador do produto para este lote", example = "10")
        @NotNull Integer productId,
        @Schema(description = "Código do lote", example = "LOTE-202601")
        String batchCode,
        @Schema(description = "Data de fabricação", example = "2026-01-01")
        LocalDate manufactureDate,
        @Schema(description = "Data de validade", example = "2026-06-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LocalDate expirationDate,
        @Schema(description = "Quantidade inicial em estoque", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero BigDecimal quantityInStock,
        @Schema(description = "Quantidade perdida", example = "0")
        @PositiveOrZero BigDecimal quantityLost,
        @Schema(description = "Preço de compra por unidade", example = "1.50", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive BigDecimal purchasePrice
        ) {
}
