package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productCategory.id", target = "categoryId")
    @Mapping(source = "productCategory.name", target = "categoryName")
    @Mapping(target = "currentProfitMargin", expression = "java(calculateCurrentProfitMargin(product))")
    ProductResponseDTO toResponse(Product product);

    @Mapping(source = "productCategory.id", target = "categoryId")
    @Mapping(source = "productCategory.name", target = "categoryName")
    @Mapping(target = "currentProfitMargin", expression = "java(calculateCurrentProfitMargin(product))")
    List<ProductResponseDTO> toResponseList(List<Product> products);


    @Mapping(target = "productCategory", ignore = true)
    @Mapping(target = "batches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "averagePrice", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalQuantity", ignore = true)
    Product toEntity(ProductRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCategory", ignore = true)
    @Mapping(target = "batches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "averagePrice", ignore = true)
    @Mapping(target = "totalQuantity", ignore = true)
    Product updateEntityFromDTO(ProductRequestDTO request, @MappingTarget Product product);

    default BigDecimal calculateCurrentProfitMargin(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) == 0 ||
            product.getAveragePrice() == null || product.getAveragePrice().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return product.getPrice()
                .divide(product.getAveragePrice(), RoundingMode.CEILING)
                .subtract(BigDecimal.ONE)
                .multiply(new BigDecimal("100.00"));
    }
}
