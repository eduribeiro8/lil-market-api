package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Detalhes do fornecedor retornados")
public record SupplierResponseDTO(
        @Schema(description = "Identificador do fornecedor", example = "1")
        Integer id,
        @Schema(description = "Nome do fornecedor", example = "Distribuidora de Bebidas LTDA")
        String name,
        @Schema(description = "Número de telefone", example = "(11) 98888-7777")
        String phoneNumber,
        @Schema(description = "Endereço", example = "Rua das Flores, 123")
        String address,
        @Schema(description = "Bairro", example = "Centro")
        String district,
        @Schema(description = "Cidade", example = "São Paulo")
        String city,
        @Schema(description = "Data/hora de criação", example = "2026-01-30T12:00:00Z")
        OffsetDateTime createdAt
) {
}
