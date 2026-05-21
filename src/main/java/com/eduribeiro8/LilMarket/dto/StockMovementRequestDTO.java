package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Filtros para consulta de movimentações de estoque")
public record StockMovementRequestDTO(
        @Schema(description = "ID do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long productId,
        @Schema(description = "Tipo da movimentação (opcional)", example = "ENTRY")
        MovementType movementType,
        @Schema(description = "Data inicial para filtro (opcional)", example = "2026-01-01")
        LocalDate startDate,
        @Schema(description = "Data final para filtro (opcional)", example = "2026-01-31")
        LocalDate endDate
) {
}
