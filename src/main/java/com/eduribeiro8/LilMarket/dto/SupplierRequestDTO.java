package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para criar/atualizar fornecedor")
public record SupplierRequestDTO(
        @Schema(description = "Nome do fornecedor", example = "Distribuidora de Bebidas LTDA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,
        @Schema(description = "Número de telefone", example = "(11) 98888-7777")
        String phoneNumber,
        @Schema(description = "Endereço", example = "Rua das Flores, 123")
        String address,
        @Schema(description = "Bairro", example = "Centro")
        String district,
        @Schema(description = "Cidade", example = "São Paulo")
        String city
) {
}
