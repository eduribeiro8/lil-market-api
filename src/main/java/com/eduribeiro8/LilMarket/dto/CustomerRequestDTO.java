package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Requisição para criar/atualizar cliente")
public record CustomerRequestDTO(
        @Schema(description = "Primeiro nome do cliente", example = "Alice", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull String firstName,
        @Schema(description = "Sobrenome do cliente", example = "Silva")
        String lastName,
        @Schema(description = "Email do cliente", example = "alice@exemplo.com")
        String email,
        @Schema(description = "Telefone do cliente", example = "+5511999999999")
        String phoneNumber,
        @Schema(description = "Endereço do cliente", example = "Rua Exemplo, 123")
        String address,
        @Schema(description = "Crédito disponível para o cliente", example = "100.00")
        @PositiveOrZero BigDecimal credit
        ) {
}
