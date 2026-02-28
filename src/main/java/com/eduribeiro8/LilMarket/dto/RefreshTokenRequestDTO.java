package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para atualizar o token de acesso")
public record RefreshTokenRequestDTO(
        @Schema(description = "Token de atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O refresh token é obrigatório")
        String refreshToken
) {
}
