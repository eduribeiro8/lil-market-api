package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductCategoryRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductCategoryResponseDTO;

import java.util.List;

public interface ProductCategoryService {

    ProductCategoryResponseDTO save(ProductCategoryRequestDTO productCategoryRequestDTO);

    ProductCategoryResponseDTO findById(int categoryId);

    ProductCategoryResponseDTO findByName(String categoryName);

    List<ProductCategoryResponseDTO> getAll();

}
