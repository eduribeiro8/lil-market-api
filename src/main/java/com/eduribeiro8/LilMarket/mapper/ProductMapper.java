package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productCategory.id", target = "categoryId")
    @Mapping(source = "productCategory.name", target = "categoryName")
    ProductResponseDTO toResponse(Product product);

    @Mapping(source = "productCategory.name", target = "categoryName")
    List<ProductResponseDTO> toResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCategory", ignore = true)
    @Mapping(target = "batches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequestDTO request);
}
