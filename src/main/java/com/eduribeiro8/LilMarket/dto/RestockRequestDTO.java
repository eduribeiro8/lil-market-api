package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Requisição para registrar reabastecimento de estoque")
public record RestockRequestDTO(
        @Schema(description = "Identificador do fornecedor", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long supplierId,
        @Schema(description = "Nota fiscal da compra", example = "55.001.12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{restock.invoice.required}") String invoice,
        @Schema(description = "Lista de lotes recebidos no reabastecimento")
        @NotEmpty @Valid List<BatchRequestDTO> batchRequestDTOS,
        @Schema(description = "Valor total pago pelo reabastecimento", example = "1500.50", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero BigDecimal amountPaid,
        @Schema(description = "Data da compra/recebimento", example = "2026-02-10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LocalDate boughtAt
) {
}
