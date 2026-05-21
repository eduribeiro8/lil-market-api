package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.StockMovementBackfillResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.StockMovementBackfillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Stock Movement Admin", description = "Endpoints administrativos de movimentação de estoque")
@SecurityRequirement(name = "bearerAuth")
public class StockMovementAdminController {

    private final StockMovementBackfillService stockMovementBackfillService;

    @PostMapping("/admin/stock-movement/backfill")
    @Operation(
            summary = "Executa backfill único da tabela stock_movement",
            description = "Popula a tabela stock_movement com base em restocks/lotes, vendas e perdas de lote. " +
                    "Este endpoint deve ser executado uma única vez."
    )
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Backfill executado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Backfill inválido ou já executado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<StockMovementBackfillResponseDTO> executeBackfill() {
        return ResponseEntity.ok(stockMovementBackfillService.executeOneTimeBackfill());
    }
}
