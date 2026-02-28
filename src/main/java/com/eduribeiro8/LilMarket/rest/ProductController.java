package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
import com.eduribeiro8.LilMarket.service.ProductService;
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
@Tag(name = "Products", description = "Endpoints para gestão de produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/id/{productId}")
    @Operation(summary = "Busca um produto por ID", description = "Retorna o produto que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDTO> findProductById(
            @Parameter(required = true, description = "ID do produto", example = "1")
            @PathVariable Long productId) {
        ProductResponseDTO product = productService.findProductByIdDTO(productId);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(product);
    }


    @GetMapping("/product/barcode/{productBarcode}")
    @Operation(summary = "Busca um produto por código de barras", description = "Retorna o produto que possui o código de barras informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDTO> findProductByBarcode(
            @Parameter(required = true, description = "Código de barras do produto", example = "0123456789012")
            @PathVariable String productBarcode) {
        ProductResponseDTO product = productService.findProductByBarcode(productBarcode);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/product")
    @Operation(summary = "Lista todos os produtos cadastrados (Paginado)",
            description = "Retorna uma página de produtos cadastrados.")
    @ApiStandardErrors
    @ApiResponse(responseCode = "200", description = "Página de produtos retornada com sucesso")
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
        return  productService.getAllProducts(pageable);
    }

    @PostMapping("/product")
    @Operation(summary = "Cria um novo produto", description = "Registra um novo produto no sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Produto já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDTO> saveProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um produto",
                    required = true
            )
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO created = productService.save(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/product/{productId}")
    @Operation(summary = "Atualiza um produto existente", description = "Atualiza os dados de um produto já cadastrado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(required = true, description = "ID do produto a ser atualizado", example = "1")
            @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do produto",
                    required = true
            )
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            throw new ProductNotFoundException();
        }
        ProductResponseDTO updated = productService.updateProduct(productId, productRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Exclui um produto", description = "Remove o produto com o ID informado do sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProductById(
            @Parameter(required = true, description = "ID do produto a ser excluído", example = "1")
            @PathVariable Long productId) {
        productService.deleteById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
