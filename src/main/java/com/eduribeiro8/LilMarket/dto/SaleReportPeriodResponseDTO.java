package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Relatório consolidado de vendas por período")
public record SaleReportPeriodResponseDTO(
        @Schema(description = "Tipo de agrupamento gerado", example = "DAILY")
        String type,
        @Schema(description = "Data de início do relatório", example = "2026-01-01")
        LocalDate startTime,
        @Schema(description = "Data de fim do relatório", example = "2026-01-31")
        LocalDate endTime,
        @Schema(description = "Total geral de vendas no período", example = "50")
        Long totalSaleCount,
        @Schema(description = "Receita total de todo o período", example = "5000.00")
        BigDecimal totalRevenue,
        @Schema(description = "Lucro líquido total de todo o período", example = "1200.00")
        BigDecimal totalNetProfit,
        @Schema(description = "Ticket médio geral do período", example = "100.00")
        BigDecimal averageTicket,
        @Schema(description = "Total de descontos concedidos em todo o período", example = "100.00")
        BigDecimal totalDiscounts,
        @Schema(description = "Lista de dados agregados por período menor")
        List<SaleReportDataPointResponseDTO> data
) {
}
