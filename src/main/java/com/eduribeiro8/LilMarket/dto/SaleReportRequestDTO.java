package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO de requisição para relatório de vendas")
public record SaleReportRequestDTO(
        @Schema(description = "Tipo de agrupamento (ex: DAILY, MONTHLY)", example = "DAILY")
        String type,
        @Schema(description = "Data de início do período", example = "2026-01-01")
        LocalDate startDate,
        @Schema(description = "Data de fim do período", example = "2026-01-31")
        LocalDate endDate,
        @Schema(description = "Lista de IDs de clientes a serem excluídos do relatório", example = "[1, 2, 3]")
        List<Long> excludedClients
) {
}
