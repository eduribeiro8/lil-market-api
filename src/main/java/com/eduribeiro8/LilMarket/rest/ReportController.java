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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints para geração de relatórios")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report/sale")
    @Operation(summary = "Gera um relatório de vendas", description = "Gera um relatório detalhado de vendas com base no período e filtros informados")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @ApiStandardErrors
    public ResponseEntity<SaleReportPeriodResponseDTO> getSaleReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para filtrar o relatório",
                    required = true
            )
            @RequestBody @Valid SaleReportRequestDTO saleReportRequestDTO
            ){
        return ResponseEntity.ok(reportService.getSaleReport(saleReportRequestDTO));
    }
}
