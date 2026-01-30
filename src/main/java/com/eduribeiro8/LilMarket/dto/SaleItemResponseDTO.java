package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item retornado como parte de uma venda")
public record SaleItemResponseDTO(
        @Schema(description = "Identificador do produto", example = "10")
        Integer productId,
        @Schema(description = "Nome do produto", example = "Leite integral")
        String productName,
        @Schema(description = "Quantidade vendida", example = "2")
        Integer quantity,
        @Schema(description = "Preço unitário", example = "3.49")
        BigDecimal unitPrice,
        @Schema(description = "Subtotal deste item", example = "6.98")
        BigDecimal subtotal,
        @Schema(description = "Identificador do lote associado, quando aplicável", example = "7")
        Integer batchId
) {}
