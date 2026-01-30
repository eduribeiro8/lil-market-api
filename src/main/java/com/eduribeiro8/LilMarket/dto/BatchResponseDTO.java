package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes do lote retornados")
public record BatchResponseDTO(
        @Schema(description = "Identificador do lote", example = "7")
        Integer batchId,
        @Schema(description = "Identificador do produto", example = "10")
        Integer productId,
        @Schema(description = "Código do lote", example = "LOTE-202601")
        String batchCode,
        @Schema(description = "Data de fabricação", example = "2026-01-01")
        LocalDate manufactureDate,
        @Schema(description = "Data de validade", example = "2026-06-01")
        LocalDate expirationDate,
        @Schema(description = "Quantidade em estoque", example = "100")
        Integer quantityInStock,
        @Schema(description = "Quantidade perdida", example = "2")
        Integer quantityLost,
        @Schema(description = "Preço de compra por unidade", example = "1.50")
        BigDecimal purchasePrice,
        @Schema(description = "Data/hora de criação", example = "2026-01-30T12:00:00Z")
        OffsetDateTime createdAt
) {
}
