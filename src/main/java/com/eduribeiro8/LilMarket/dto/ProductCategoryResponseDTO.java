package com.eduribeiro8.LilMarket.dto;

import java.time.OffsetDateTime;

public record ProductCategoryResponseDTO(
        Integer id,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
