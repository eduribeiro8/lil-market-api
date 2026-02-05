package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para criar/atualizar produto")
public record ProductRequestDTO(
        @Schema(description = "Nome do produto", example = "Leite integral", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,
        @Schema(description = "Código de barras (único)", example = "1234567890123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String barcode,
        @Schema(description = "Descrição do produto", example = "Leite integral 1L")
        String description,
        @Schema(description = "Preço unitário", example = "3.49", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive BigDecimal price,
        @Schema(description = "ID da categoria do produto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Integer categoryId,
        @Schema(description = "Se o produto é perecível", example = "true")
        @NotNull
        Boolean isPerishable
) {}