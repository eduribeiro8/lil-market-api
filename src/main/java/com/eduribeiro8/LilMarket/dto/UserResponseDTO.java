package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.UserRole;

import java.time.OffsetDateTime;

public record UserResponseDTO(
        Integer id,
        String username,
        String firstName,
        UserRole userRole,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime lastLogin
) {
}
