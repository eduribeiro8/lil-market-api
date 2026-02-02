package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload para login do usuário")
public record LoginRequestDTO(
        @Schema(description = "Nome de usuário", example = "jdoe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String username,
        @Schema(description = "Senha", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String password
) {
}
