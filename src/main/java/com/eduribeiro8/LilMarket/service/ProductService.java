package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchSimulationResponseDTO;
import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    Page<ProductResponseDTO> getAllProductsStartsWith(String startsWith, Pageable pageable);

    ProductResponseDTO findProductByIdDTO(Long productId);

    Product findProductById(Long productId);

    ProductResponseDTO save(ProductRequestDTO theProduct);

    ProductResponseDTO findProductByBarcode(String productBarcode);

    ProductResponseDTO updateProduct(Long productId, ProductRequestDTO theProduct);

    void deleteById(Long productId);

    void calculatePriceBasedOnStock(Long productId);

    BatchSimulationResponseDTO simulatePricing(Long productId, java.math.BigDecimal newQuantity, java.math.BigDecimal newPurchasePrice);

    void updatePrice(Long productId, java.math.BigDecimal newPrice);
}
