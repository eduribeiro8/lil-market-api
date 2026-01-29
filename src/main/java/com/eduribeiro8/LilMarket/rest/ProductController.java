package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
import com.eduribeiro8.LilMarket.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/id/{productId}")
    ResponseEntity<ProductResponseDTO> findProductById(@PathVariable int productId){
        ProductResponseDTO theProduct = productService.findProductByIdDTO(productId);
        if(theProduct == null){
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(theProduct);
    }

    @GetMapping("/product/barcode/{productBarcode}")
    ResponseEntity<ProductResponseDTO> findProductByBarcode(@PathVariable String productBarcode){
        ProductResponseDTO theProduct = productService.findProductByBarcode(productBarcode);

        return ResponseEntity.ok(theProduct);
    }

    @PostMapping("/product")
    ResponseEntity<ProductResponseDTO> saveProduct(@Valid @RequestBody ProductRequestDTO theProduct){
        ProductResponseDTO productResponseDTO = productService.save(theProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @PutMapping("/product")
    ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody ProductRequestDTO theProduct){
        if (theProduct == null){
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(productService.updateProduct(theProduct));
    }

    @GetMapping("/product")
    public List<ProductResponseDTO> getAllProducts(){
        return productService.findAllProducts();
    }
}
