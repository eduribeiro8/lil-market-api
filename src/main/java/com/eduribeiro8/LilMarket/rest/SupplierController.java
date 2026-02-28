package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.SupplierRequestDTO;
import com.eduribeiro8.LilMarket.dto.SupplierResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.SupplierService;
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
@Tag(name = "Suppliers", description = "Endpoints para gestão de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/supplier")
    @Operation(summary = "Cria um novo fornecedor", description = "Registra um novo fornecedor no sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Fornecedor já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SupplierResponseDTO> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um fornecedor",
                    required = true
            )
            @Valid @RequestBody SupplierRequestDTO supplierRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.save(supplierRequestDTO));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Busca um fornecedor por ID", description = "Retorna o fornecedor que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SupplierResponseDTO> findById(
            @Parameter(required = true, description = "ID do fornecedor", example = "1")
            @PathVariable Long supplierId){
        return ResponseEntity.ok(supplierService.findByIdDTO(supplierId));
    }

    @GetMapping("/supplier")
    @Operation(summary = "Lista todos os fornecedores (Paginado)", description = "Retorna uma página de fornecedores cadastrados")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Página de fornecedores retornada com sucesso")
    public Page<SupplierResponseDTO> getAll(Pageable pageable){
        return supplierService.getAll(pageable);
    }
}
