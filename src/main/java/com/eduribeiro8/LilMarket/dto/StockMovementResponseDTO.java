package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Resposta de uma movimentação de estoque")
public record StockMovementResponseDTO(
        @Schema(description = "ID da movimentação", example = "10")
        Long id,
        @Schema(description = "ID do produto", example = "1")
        Long productId,
        @Schema(description = "Nome do produto", example = "Coca Cola Retornável 2 Litros")
        String productName,
        @Schema(description = "Tipo da movimentação", example = "EXIT")
        MovementType movementType,
        @Schema(description = "Quantidade movimentada", example = "2.000")
        BigDecimal quantity,
        @Schema(description = "Saldo em estoque após a movimentação", example = "35.000")
        BigDecimal quantityInStock,
        @Schema(description = "ID de referência da origem da movimentação (sale/restock/batch)", example = "123")
        Long referenceId,
        @Schema(description = "Descrição da movimentação", example = "Saída de estoque por venda")
        String description,
        @Schema(description = "Data/hora da movimentação em UTC", example = "2026-03-10T04:00:27Z")
        OffsetDateTime timestamp
) {
}
