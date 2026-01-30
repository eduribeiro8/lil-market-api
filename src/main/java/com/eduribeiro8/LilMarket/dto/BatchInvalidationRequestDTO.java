package com.eduribeiro8.LilMarket.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para invalidar um lote (ex.: remover do estoque)")
public record BatchInvalidationRequestDTO(
        @Schema(description = "Motivo da invalidação", example = "Estoque vencido", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String reason
) {}
