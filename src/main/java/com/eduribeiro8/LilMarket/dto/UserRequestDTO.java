package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para criação de usuário")
public record UserRequestDTO(
        @Schema(description = "Nome de usuário único", example = "jdoe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,
        @Schema(description = "Senha (mínimo 6 caracteres)", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 6) String password,
        @Schema(description = "Primeiro nome", example = "João", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String firstName,
        @Schema(description = "Papel do usuário", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UserRole userRole,
        @Schema(description = "Se o usuário está ativo", example = "true")
        Boolean active
) {
}
