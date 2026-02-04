package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductCategoryRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductCategoryResponseDTO;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import com.eduribeiro8.LilMarket.mapper.ProductCategoryMapper;
import com.eduribeiro8.LilMarket.repository.ProductCategoryRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateProductCategoryException;
import com.eduribeiro8.LilMarket.rest.exception.ProductCategoryNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Override
    @Transactional
    public ProductCategoryResponseDTO save(ProductCategoryRequestDTO productCategoryRequestDTO) {
        if (productCategoryRepository.existsByName(productCategoryRequestDTO.name())){
            throw new DuplicateProductCategoryException("Category " + productCategoryRequestDTO.name() + " is already registered!");
        }

        ProductCategory productCategoryToSave = productCategoryMapper.toEntity(productCategoryRequestDTO);

        ProductCategory productCategory = productCategoryRepository.save(productCategoryToSave);

        return productCategoryMapper.toResponse(productCategory);
    }

    @Override
    public ProductCategoryResponseDTO findById(int categoryId) {
        ProductCategory productCategory = productCategoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ProductCategoryNotFoundException("Category (id = " + categoryId + ") not found!"));

        return productCategoryMapper.toResponse(productCategory);
    }

    @Override
    public ProductCategoryResponseDTO findByName(String categoryName) {
        ProductCategory productCategory = productCategoryRepository
                .findByName(categoryName)
                .orElseThrow(() -> new ProductCategoryNotFoundException("Category " + categoryName + " not found!"));

        return productCategoryMapper.toResponse(productCategory);
    }

    @Override
    public List<ProductCategoryResponseDTO> getAll() {
        List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
        return productCategoryMapper.toResponseList(productCategoryList);
    }
}
