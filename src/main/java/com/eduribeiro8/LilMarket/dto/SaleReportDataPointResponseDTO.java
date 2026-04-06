package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;

public record SaleReportDataPointResponseDTO(
        String label,
        Long saleCount,
        BigDecimal revenue,
        BigDecimal netProfit,
        BigDecimal averageTicket,
        BigDecimal discount
){}
