package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO findProductByIdDTO(int productId);

    Product findProductById(int productId);

    ProductResponseDTO save(ProductRequestDTO theProduct);

    ProductResponseDTO findProductByBarcode(String productBarcode);

    ProductResponseDTO updateProduct(int productId, ProductRequestDTO theProduct);

    void deleteById(int productId);
}
