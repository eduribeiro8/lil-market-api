package com.eduribeiro8.LilMarket.mapper;

import com.eduribeiro8.LilMarket.dto.CustomerPaymentRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerPaymentResponseDTO;
import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerPaymentMapper {
    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "customer.id", target = "customerId")
    CustomerPaymentResponseDTO toResponse(CustomerPayment customerPayment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    CustomerPayment toEntity(CustomerPaymentRequestDTO request);
}
