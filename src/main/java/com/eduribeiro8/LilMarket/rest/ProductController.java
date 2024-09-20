package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
import com.eduribeiro8.LilMarket.service.ProductService;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
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
    ResponseEntity<Product> getProductById(@PathVariable int productId){
        Product theProduct = productService.findProductById(productId);
        if(theProduct == null){
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(theProduct);
    }

    @GetMapping("/product/barcode/{productBarcode}")
    ResponseEntity<Product> getProductByBarcode(@PathVariable String productBarcode){
        Product theProduct = productService.findProductByBarcode(productBarcode);
        if (theProduct == null){
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(theProduct);
    }

    @PostMapping("/product")
    ResponseEntity<String> saveProduct(@Valid @RequestBody Product theProduct){
        productService.save(theProduct);
        return ResponseEntity.ok("Product was successfully saved!");
    }

    @PutMapping("/product")
    ResponseEntity<Product> updateProduct(@RequestBody Product theProduct){
        if (theProduct == null){
            throw new ProductNotFoundException();
        }
        return ResponseEntity.ok(productService.updateProduct(theProduct));
    }

    @GetMapping("/product")
    public List<Product> getAllProducts(){
        return productService.findAllProducts();
    }
}
