package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.SupplierRequestDTO;
import com.eduribeiro8.LilMarket.dto.SupplierResponseDTO;
import com.eduribeiro8.LilMarket.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierResponseDTO toResponse(Supplier supplier);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Supplier toEntity(SupplierRequestDTO supplierRequestDTO);

}
