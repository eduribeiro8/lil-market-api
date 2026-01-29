package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.ProductCategoryRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductCategoryResponseDTO;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "string")
public interface ProductCategoryMapper {

    ProductCategoryResponseDTO toResponse(ProductCategory productCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductCategory toEntity(ProductCategoryRequestDTO request);
}
