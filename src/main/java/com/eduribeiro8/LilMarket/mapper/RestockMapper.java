package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import com.eduribeiro8.LilMarket.entity.Restock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestockMapper {
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    RestockResponseDTO toResponse(Restock restock);

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    List<RestockResponseDTO> toResponseList(List<Restock> restockList);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Restock toEntity(RestockRequestDTO restockRequestDTO);

}
