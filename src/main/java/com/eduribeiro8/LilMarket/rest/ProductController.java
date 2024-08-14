package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Product getProductById(@PathVariable int productId){
        return productService.findProductById(productId);
    }

    @GetMapping("/product/barcode/{productBarcode}")
    public Product getProductByBarcode(@PathVariable long productBarcode){
        return productService.findProductByBarcode(productBarcode);
    }

    @PostMapping("/product")
    public Product saveProduct(@RequestBody Product theProduct){
        if (theProduct == null){
            throw new RuntimeException("product is empty!");
        }

        return productService.save(theProduct);
    }

    @PutMapping("/product")
    public Product updateProduct(@RequestBody Product theProduct){
        if (theProduct == null){
            throw new RuntimeException("product is empty!");
        }

        return productService.updateProduct(theProduct);
    }

    @GetMapping("/product")
    public List<Product> getAllProducts(){
        return productService.findAllProducts();
    }
}
