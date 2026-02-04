package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para criar/atualizar categoria de produto")
public record ProductCategoryRequestDTO(
        @Schema(description = "Nome da categoria", example = "Laticínios", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,
        @Schema(description = "Descrição da categoria", example = "Leites e derivados")
        String description
) {
}
