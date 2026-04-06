package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleReportPeriodResponseDTO(
        String type,
        LocalDate startTime,
        LocalDate endTime,
        Long totalSaleCount,
        BigDecimal totalRevenue,
        BigDecimal totalNetProfit,
        BigDecimal averageTicket,
        BigDecimal totalDiscounts,
        List<SaleReportDataPointResponseDTO> data
) {
}
