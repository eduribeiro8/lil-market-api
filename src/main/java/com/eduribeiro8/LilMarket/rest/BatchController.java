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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Batches", description = "Endpoints para gestão de lotes")
@SecurityRequirement(name = "basicScheme")
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "Cria um novo lote",
            description = "Registra um novo lote no sistema.")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Lote já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/batch")
    public BatchResponseDTO save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um lote.",
                    required = true
            )
            @Valid @RequestBody BatchRequestDTO batchRequestDTO) {
        return batchService.save(batchRequestDTO);
    }

    @Operation(summary = "Lista todos os lotes em estoque",
            description = "Retorna a lista de todos os lotes que ainda têm quantidade em estoque.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Lista de lotes retornada com sucesso")
    @GetMapping("/batch")
    public List<BatchResponseDTO> getAllBatchesInStock() {
        return batchService.getAllBatchesInStock();
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
            @RequestParam(defaultValue = "1") Integer quantity) {
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
