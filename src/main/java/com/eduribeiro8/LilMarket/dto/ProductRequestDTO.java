package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;

import com.eduribeiro8.LilMarket.entity.UnitType;
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
        BigDecimal price,
        @Schema(description = "Se o produto atualiza o preço automaticamente", example = "true")
        Boolean autoPricing,
        @Schema(description = "Margem de lucro em porcentagem", example = "50.0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero BigDecimal profitMargin,
        @Schema(description = "Quantidade mínima em estoque para alerta", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero Integer minQuantityInStock,
        @Schema(description = "ID da categoria do produto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Integer categoryId,
        @Schema(description = "Tipo de unidade", example = "WEIGHT")
        @NotNull UnitType unitType,
        @Schema(description = "Se o produto é perecível", example = "true")
        @NotNull
        Boolean isPerishable,
        @Schema(description = "Se o alerta de estoque baixo está ativo", example = "true")
        @NotNull Boolean alert
) {}
