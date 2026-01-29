package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.SaleItemRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleItemResponseDTO;
import com.eduribeiro8.LilMarket.entity.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleItemMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "batch.id", target = "batchId")
    SaleItemResponseDTO toResponse(SaleItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sale", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    SaleItem toEntity(SaleItemRequestDTO request);
}
