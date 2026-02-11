package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.RestockService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Restocks", description = "Endpoints para gestão de reposições de estoque (compras)")
@SecurityRequirement(name = "basicScheme")
public class RestockController {

    private final RestockService restockService;

    @PostMapping("/restock")
    @Operation(summary = "Registra uma nova reposição de estoque", description = "Registra uma nova compra de produtos para reposição do estoque")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reposição registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RestockResponseDTO> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para registrar uma reposição",
                    required = true
            )
            @Valid @RequestBody RestockRequestDTO restockRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(restockService.save(restockRequestDTO));
    }

    @GetMapping("/restock/{restockId}")
    @Operation(summary = "Busca uma reposição por ID", description = "Retorna a reposição que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reposição encontrada"),
            @ApiResponse(responseCode = "404", description = "Reposição não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RestockResponseDTO> getById(
            @Parameter(required = true, description = "ID da reposição", example = "1")
            @PathVariable Integer restockId){
        return ResponseEntity.ok(restockService.findById(restockId));
    }

    @GetMapping("/restock")
    @Operation(summary = "Lista todas as reposições (Paginado)", description = "Retorna uma página de reposições registradas")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Página de reposições retornada com sucesso")
    public Page<RestockResponseDTO> getAll(Pageable pageable){
        return restockService.getAll(pageable);
    }
}
