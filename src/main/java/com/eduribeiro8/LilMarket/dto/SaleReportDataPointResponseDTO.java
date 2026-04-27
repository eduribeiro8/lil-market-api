package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Ponto de dados do relatório de vendas")
public record SaleReportDataPointResponseDTO(
        @Schema(description = "Rótulo do período (ex: 2026-01-01)", example = "2026-01-01")
        String label,
        @Schema(description = "Quantidade de vendas no período", example = "10")
        Long saleCount,
        @Schema(description = "Receita total gerada no período", example = "1500.50")
        BigDecimal revenue,
        @Schema(description = "Lucro líquido do período", example = "300.25")
        BigDecimal netProfit,
        @Schema(description = "Ticket médio das vendas", example = "150.05")
        BigDecimal averageTicket,
        @Schema(description = "Total de descontos concedidos", example = "50.00")
        BigDecimal discount
){}
