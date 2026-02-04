package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import com.eduribeiro8.LilMarket.mapper.ProductMapper;
import com.eduribeiro8.LilMarket.repository.ProductCategoryRepository;
import com.eduribeiro8.LilMarket.repository.ProductRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBarcodeException;
import com.eduribeiro8.LilMarket.rest.exception.ProductCategoryNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

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
    public ProductResponseDTO findProductByIdDTO(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
        return productMapper.toResponse(product);
    }

    @Override
    public Product findProductById(int productId) {
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
    public ProductResponseDTO updateProduct(ProductRequestDTO theProduct) {
        Product productToSave = productMapper.toEntity(theProduct);

        ProductCategory productCategory = productCategoryRepository
                .findById(theProduct.categoryId())
                .orElseThrow(ProductCategoryNotFoundException::new);

        productToSave.setProductCategory(productCategory);

        Product savedProduct = productRepository.save(productToSave);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteById(int productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));

        productRepository.delete(product);
    }
}
