package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Detalhes do reabastecimento retornados")
public record RestockResponseDTO(
        @Schema(description = "Identificador do reabastecimento", example = "1")
        Long id,
        @Schema(description = "Identificador do fornecedor", example = "1")
        Long supplierId,
        @Schema(description = "Nome do fornecedor", example = "Distribuidora de Bebidas LTDA")
        String supplierName,
        @Schema(description = "Valor total pago", example = "1500.50")
        BigDecimal amountPaid,
        @Schema(description = "Data da compra/recebimento", example = "2026-02-10")
        LocalDate boughtAt,
        @Schema(description = "Data/hora de criação do registro", example = "2026-02-11T10:00:00Z")
        OffsetDateTime createdAt
) {
}
