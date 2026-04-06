package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.SaleReportPeriodResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportRequestDTO;
import com.eduribeiro8.LilMarket.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report/sale")
    @ApiStandardErrors
    public ResponseEntity<SaleReportPeriodResponseDTO> getSaleReport(
            @RequestBody @Valid SaleReportRequestDTO saleReportRequestDTO
            ){
        return ResponseEntity.ok(reportService.getSaleReport(saleReportRequestDTO));
    }
}
