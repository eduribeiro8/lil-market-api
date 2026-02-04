package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.ProductCategoryRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductCategoryResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.ProductCategoryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Product Categories", description = "Endpoints para gestão de categorias de produtos")
@SecurityRequirement(name = "basicScheme")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping("/category")
    @Operation(summary = "Cria uma nova categoria de produto", description = "Registra uma nova categoria no sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Categoria já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductCategoryResponseDTO> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar uma categoria",
                    required = true
            )
            @Valid @RequestBody ProductCategoryRequestDTO categoryRequestDTO){
        ProductCategoryResponseDTO category = productCategoryService.save(categoryRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(category);
    }

    @GetMapping("/category")
    @Operation(summary = "Lista todas as categorias", description = "Retorna uma lista de todas as categorias de produtos cadastradas")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<List<ProductCategoryResponseDTO>> getAllCategories(){
        return ResponseEntity.ok(productCategoryService.getAll());
    }

    @GetMapping("/category/id/{categoryId}")
    @Operation(summary = "Busca uma categoria por ID", description = "Retorna a categoria que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductCategoryResponseDTO> findById(
            @Parameter(required = true, description = "ID da categoria", example = "1")
            @PathVariable int categoryId){
        return ResponseEntity.ok(productCategoryService.findById(categoryId));
    }

    @GetMapping("/category/name/{categoryName}")
    @Operation(summary = "Busca uma categoria por nome", description = "Retorna a categoria que possui o nome informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductCategoryResponseDTO> findById(
            @Parameter(required = true, description = "Nome da categoria", example = "Bebidas")
            @PathVariable String categoryName){
        return ResponseEntity.ok(productCategoryService.findByName(categoryName));
    }
}
