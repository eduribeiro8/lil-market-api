package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações do cliente retornadas")
public record CustomerResponseDTO(
        @Schema(description = "Identificador do cliente", example = "5")
        Long id,
        @Schema(description = "Primeiro nome", example = "Alice")
        String firstName,
        @Schema(description = "Sobrenome", example = "Silva")
        String lastName,
        @Schema(description = "Email", example = "alice@exemplo.com")
        String email,
        @Schema(description = "Telefone", example = "+5511999999999")
        String phoneNumber,
        @Schema(description = "Endereço", example = "Rua Exemplo, 123")
        String address,
        @Schema(description = "Crédito", example = "100.00")
        BigDecimal credit,
        @Schema(description = "Data/hora de criação", example = "2026-01-30T12:00:00Z")
        OffsetDateTime createdAt
) {
}
