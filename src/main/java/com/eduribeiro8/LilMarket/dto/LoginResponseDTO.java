package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de login do usuário")
public record LoginResponseDTO(
        @Schema(description = "Identificador do usuário", example = "1")
        Long id,
        @Schema(description = "Nome de usuário", example = "jdoe")
        String username,
        @Schema(description = "Papel do usuário", example = "ADMIN")
        UserRole userRole,
        @Schema(description = "Token JWT do usuário")
        String token,
        @Schema(description = "Tempo restante para o token expirar (em milissegundos)")
        long expiresIn
) {
}
