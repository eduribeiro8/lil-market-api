package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.BatchService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Batches", description = "Endpoints para gestão de lotes")
@SecurityRequirement(name = "bearerAuth")
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "Lista todos os lotes em estoque (Paginado)",
            description = "Retorna uma página de lotes que ainda têm quantidade em estoque.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Página de lotes retornada com sucesso")
    @GetMapping("/batch")
    public Page<BatchResponseDTO> getAllBatchesInStock(Pageable pageable) {
        return batchService.getAllBatchesInStock(pageable);
    }

    @Operation(summary = "Lista todos os lotes em estoque  entre um intarvalo (Paginado)",
            description = "Retorna uma página de lotes que ainda têm quantidade em estoque no intervalo requisitado.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Página de lotes retornada com sucesso")
    @GetMapping("/batch/by-date")
    public Page<BatchResponseDTO> getAllBatchesInStockByDate(
            @Parameter(required = true, description = "Data inicial", example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(required = true, description = "Data final", example = "2026-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        return batchService.getAllBatchesInStockByDate(startDate, endDate, pageable);
    }


    @Operation(summary = "Busca um lote por ID", description = "Retorna o lote que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lote encontrado"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/batch/{batchId}")
    public BatchResponseDTO getBatch(
            @Parameter(required = true, description = "ID do lote", example = "1")
            @Valid @PathVariable int batchId){
        return batchService.getBatchById(batchId);
    }

    @GetMapping("/batch/restock/{restockId}")
    public Page<BatchResponseDTO> getAllBatchesByRestockId(
            @Parameter(required = true, description = "ID do lote", example = "1")
            @Valid @PathVariable int restockId,
            Pageable pageable){
        return batchService.getAllBatchesByRestockId(restockId, pageable);
    }

    @Operation(summary = "Busca lotes em estoque por produto e quantidade mínima",
            description = "Retorna os lotes de um produto que têm quantidade em estoque maior ou igual ao valor informado, ordenados pelo prazo de validade mais próximo.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Lista de lotes retornada com sucesso")
    @GetMapping("/batch/in-stock")
    public List<BatchResponseDTO> getBatchesInStock(
            @Parameter(required = true, description = "ID do produto", example = "1")
            @RequestParam Integer productId,
            @Parameter(required = false, description = "Quantidade mínima em estoque", example = "10")
            @RequestParam(defaultValue = "1") BigDecimal quantity) {
        return batchService.getBatchesInStockDTO(productId, quantity);
    }

    @Operation(summary = "Relatório de perda de estoque",
            description = "Registra a perda de quantidade de um lote, reduzindo o estoque disponível.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "204", description = "Perda registrada com sucesso")
    @PostMapping("/batch/report-loss")
    public ResponseEntity<Void> reportLoss(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para registrar a perda.",
                    required = true
            )
            @Valid @RequestBody BatchLossReportRequestDTO request) {
        batchService.reportLoss(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Invalida estoque de um lote",
            description = "Marca um lote como inválido, removendo-o do estoque disponível.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "204", description = "Estoque invalidado com sucesso")
    @PostMapping("/batch/invalidate-stock")
    public ResponseEntity<Void> invalidateStock(
            @Parameter(required = true, description = "ID do lote a ser invalidado", example = "5")
            @RequestParam Integer batchId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados adicionais para a invalidação.",
                    required = true
            )
            @Valid @RequestBody BatchInvalidationRequestDTO request) {
        batchService.invalidateBatch(batchId, request);
        return ResponseEntity.noContent().build();
    }
}
