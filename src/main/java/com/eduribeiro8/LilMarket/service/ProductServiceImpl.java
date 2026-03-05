package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import com.eduribeiro8.LilMarket.mapper.ProductMapper;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.repository.ProductCategoryRepository;
import com.eduribeiro8.LilMarket.repository.ProductRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBarcodeException;
import com.eduribeiro8.LilMarket.rest.exception.ProductCategoryNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    private final BatchRepository batchRepository;

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO requestDTO) {

        if (productRepository.existsByBarcode(requestDTO.barcode().trim())){
            throw new DuplicateBarcodeException("Barcode (" + requestDTO.barcode() + ") is already registered");
        }

        ProductCategory productCategory = productCategoryRepository
                .findById(requestDTO.categoryId())
                .orElseThrow(ProductCategoryNotFoundException::new);


        Product productToSave = productMapper.toEntity(requestDTO);
        productToSave.setProductCategory(productCategory);
        productToSave.setBarcode(requestDTO.barcode().trim());

        Product product = productRepository.save(productToSave);

        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponseDTO findProductByIdDTO(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
        return productMapper.toResponse(product);
    }

    @Override
    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
    }

    @Override
    public ProductResponseDTO findProductByBarcode(String productBarcode) {
        return productRepository.findByBarcode(productBarcode)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product with barcode " + productBarcode + " not found"));
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product(id = " + productId + ") not found!"));

        if (!Objects.equals(product.getProductCategory().getId(), productRequestDTO.categoryId())){
            ProductCategory productCategory = productCategoryRepository
                    .findById(productRequestDTO.categoryId())
                    .orElseThrow(() -> new ProductCategoryNotFoundException(
                            "Category(id = " + productRequestDTO.categoryId() + ") not found!"
                    ));
            product.setProductCategory(productCategory);
        }

        Product updatedProduct = productMapper.updateEntityFromDTO(productRequestDTO, product);

        return productMapper.toResponse(productRepository.save(updatedProduct));
    }

    @Override
    @Transactional
    public void deleteById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void calculatePriceBasedOnStock(Long productId) {
        Product product = findProductById(productId);

        BigDecimal averageCost = batchRepository.calculateAverageCostByProduct(productId);

        if (averageCost == null){
            //estoque vazio
            return;
        }

        product.setAveragePrice(averageCost.setScale(2, RoundingMode.HALF_UP));

        if (product.getAutoPricing()){
            BigDecimal profitMargin = BigDecimal.ONE.add(
                    product.getProfitMargin().divide(
                            new BigDecimal(100), 2, RoundingMode.HALF_UP
                    )
            );

            BigDecimal newPrice = averageCost.multiply(profitMargin);

            BigDecimal finalPrice = newPrice.setScale(2, RoundingMode.HALF_UP);

            product.setPrice(finalPrice);
        }

        productRepository.save(product);
    }
}
