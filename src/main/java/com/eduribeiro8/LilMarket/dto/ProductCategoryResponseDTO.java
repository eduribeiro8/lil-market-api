package com.eduribeiro8.LilMarket.dto;

import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações da categoria retornadas")
public record ProductCategoryResponseDTO(
        @Schema(description = "Identificador da categoria", example = "3")
        Long id,
        @Schema(description = "Nome da categoria", example = "Laticínios")
        String name,
        @Schema(description = "Descrição da categoria", example = "Leites e derivados")
        String description,
        @Schema(description = "Data/hora de criação", example = "2026-01-30T12:00:00Z")
        OffsetDateTime createdAt,
        @Schema(description = "Data/hora de atualização", example = "2026-01-30T12:00:00Z")
        OffsetDateTime updatedAt
) {
}
