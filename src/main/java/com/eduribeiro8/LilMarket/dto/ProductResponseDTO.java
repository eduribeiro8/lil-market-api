package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;

import com.eduribeiro8.LilMarket.entity.UnitType;
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
        @Schema(description = "Quantidade total do produto em estoque", example = "100")
        BigDecimal totalQuantity,
        @Schema(description = "Se o produto atualiza o preço automaticamente", example = "true")
        Boolean autoPricing,
        @Schema(description = "Margem de lucro em porcentagem", example = "50.0")
        BigDecimal profitMargin,
        @Schema(description = "Quantidade mínima em estoque para alerta", example = "10")
        Integer minQuantityInStock,
        @Schema(description = "Id da categoria", example = "1")
        String categoryId,
        @Schema(description = "Tipo de unidade", example = "WEIGHT")
        UnitType unitType,
        @Schema(description = "Nome da categoria", example = "Laticínios")
        String categoryName,
        @Schema(description = "Se o produto é perecível", example = "true")
        Boolean isPerishable,
        @Schema(description = "Se o alerta de estoque baixo está ativo", example = "true")
        Boolean alert
) {}
