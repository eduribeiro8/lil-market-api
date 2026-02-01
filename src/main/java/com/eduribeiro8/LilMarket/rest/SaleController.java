package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Endpoints para gestão de vendas e baixa de estoque")
@SecurityRequirement(name = "basicScheme")
public class SaleController {

    private final SaleService saleService;

    @Operation(summary = "Busca uma venda por id", description = "Retorna a venda que tem o id requisitado")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Retorna a venda")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<SaleResponseDTO> getSaleById(
            @Parameter(required = true, description = "Id da venda", example = "123")
            @PathVariable int saleId){
        return ResponseEntity.ok(saleService.findSaleById(saleId));
    }

    @Operation(summary = "Busca vendas que ocorreram entre um intervalo de datas",
                description = "Retorna todas as vendas realizadas entre duas datas. As datas devem seguir o formato ISO (yyyy-MM-dd).")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Retorna as vendas que aconteceram no intervalo")
    @ApiResponse(responseCode = "400", description = "Erro caso a data final for antecedente a data inicial", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Erro caso não há vendas no período", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/sale/by-date")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByDate(
            @Parameter(required = true, description = "Data inicial", example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(required = true, description = "Data final", example = "2026-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        return ResponseEntity.ok(saleService.getSalesByDate(start, end));
    }

    @Operation(summary = "Registra uma nova venda",
            description = "Processa itens, baixa estoque dos lotes (FEFO) e atualiza débito do cliente se for fiado.")
    @ApiStandardErrors
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venda criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Produto, lote ou cliente não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Erro de negócio (Estoque insuficiente ou limite de crédito excedido)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sale")
    public ResponseEntity<SaleResponseDTO> saveSale(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar uma venda.",
                    required = true
            )
            @Valid @RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO savedSale = saleService.save(saleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
    }


}
