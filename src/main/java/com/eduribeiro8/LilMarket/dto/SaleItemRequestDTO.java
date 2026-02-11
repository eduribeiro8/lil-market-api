package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item incluído em uma requisição de venda")
public record SaleItemRequestDTO(
        @Schema(description = "Identificador do produto", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Integer productId,
        @Schema(description = "Quantidade do produto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Integer quantity
) {}
