package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Resposta do usuário")
public record UserResponseDTO(
        @Schema(description = "Identificador do usuário", example = "1")
        Integer id,
        @Schema(description = "Nome de usuário", example = "jdoe")
        String username,
        @Schema(description = "Primeiro nome", example = "João")
        String firstName,
        @Schema(description = "Papel atribuído", example = "ADMIN")
        UserRole userRole,
        @Schema(description = "Se o usuário está ativo", example = "true")
        boolean active,
        @Schema(description = "Data/hora de criação", example = "2026-01-30T12:00:00Z")
        OffsetDateTime createdAt,
        @Schema(description = "Data/hora do último login", example = "2026-01-30T12:00:00Z")
        OffsetDateTime lastLogin
) {
}
