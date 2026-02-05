package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes do produto retornados")
public record ProductResponseDTO (
        @Schema(description = "Identificador do produto", example = "10")
        Integer id,
        @Schema(description = "Nome do produto", example = "Leite integral")
        String name,
        @Schema(description = "Código de barras do produto", example = "1234567890123")
        String barcode,
        @Schema(description = "Descrição do produto", example = "Leite integral 1L")
        String description,
        @Schema(description = "Preço unitário", example = "3.49")
        BigDecimal price,
        @Schema(description = "Id da categoria", example = "1")
        String categoryId,
        @Schema(description = "Nome da categoria", example = "Laticínios")
        String categoryName,
        @Schema(description = "Se o produto é perecível", example = "true")
        boolean isPerishable
) {}
