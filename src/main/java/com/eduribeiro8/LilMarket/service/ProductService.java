package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> findAllProducts();

    ProductResponseDTO findProductByIdDTO(int productId);

    Product findProductById(int productId);

    ProductResponseDTO save(ProductRequestDTO theProduct);

    ProductResponseDTO findProductByBarcode(String productBarcode);

    ProductResponseDTO updateProduct(ProductRequestDTO theProduct);

    void deleteById(int productId);
}
