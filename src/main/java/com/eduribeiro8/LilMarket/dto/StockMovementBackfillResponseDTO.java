package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da execução do backfill de movimentação de estoque")
public record StockMovementBackfillResponseDTO(
        @Schema(description = "Nome do job executado", example = "stock_movement_backfill_v1")
        String jobName,
        @Schema(description = "Quantidade de movimentações inseridas", example = "120")
        Integer movementsInserted,
        @Schema(description = "Quantidade de produtos validados", example = "35")
        Integer productsValidated,
        @Schema(description = "Mensagem de retorno", example = "Backfill executado com sucesso.")
        String message
) {
}
