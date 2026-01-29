package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SaleItemMapper.class})
public interface SaleMapper {
    @Mapping(source = "customer.firstName", target = "customerName")
    @Mapping(source = "user.firstName", target = "sellerName")
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "change", expression = "java(calculateChange(sale))")
    SaleResponseDTO toResponse(Sale sale);

    @Mapping(source = "customer.firstName", target = "customerName")
    @Mapping(source = "user.firstName", target = "sellerName")
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "change", expression = "java(calculateChange(sale))")
    List<SaleResponseDTO> toResponseList(List<Sale> sales);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "total", ignore = true)
    Sale toEntity(SaleRequestDTO request);

    default BigDecimal calculateChange(Sale sale) {
        if (sale.getAmountPaid() == null || sale.getTotal() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal change = sale.getAmountPaid().subtract(sale.getTotal());

        return change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO;
    }
}