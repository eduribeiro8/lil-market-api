package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(source = "id", target="batchId")
    @Mapping(source = "product.id", target="productId")
    @Mapping(source = "product.name", target="productName")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    BatchResponseDTO toResponse(Batch batch);

    @Mapping(source = "id", target="batchId")
    @Mapping(source = "product.id", target="productId")
    List<BatchResponseDTO> toResponseList(List<Batch> batches);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Batch toEntity(BatchRequestDTO request);
}
