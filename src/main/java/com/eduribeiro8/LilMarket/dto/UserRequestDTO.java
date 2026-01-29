package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String firstName,
        @NotNull UserRole userRole,
        Boolean active
) {
}
