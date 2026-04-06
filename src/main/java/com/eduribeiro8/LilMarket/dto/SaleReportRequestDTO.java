package com.eduribeiro8.LilMarket.dto;

import java.time.LocalDate;
import java.util.List;

public record SaleReportRequestDTO(
        String type,
        LocalDate startDate,
        LocalDate endDate,
        List<Long> excludedClients
) {
}
