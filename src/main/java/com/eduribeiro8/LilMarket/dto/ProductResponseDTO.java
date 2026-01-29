package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;

public record ProductResponseDTO (
        Integer id,
        String name,
        BigDecimal price,
        String categoryName
) {}
