package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleReportPeriodResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportRequestDTO;

public interface ReportService {

    SaleReportPeriodResponseDTO getSaleReport(SaleReportRequestDTO saleReportRequestDTO);
}
