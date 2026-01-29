package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CustomerResponseDTO(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String address,
        BigDecimal credit,
        OffsetDateTime createdAt
) {
}
