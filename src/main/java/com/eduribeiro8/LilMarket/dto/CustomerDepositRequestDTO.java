package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para realizar um depósito na conta de um cliente")
public record CustomerDepositRequestDTO(
        @Schema(description = "ID do usuário que está realizando a operação", example = "1")
        Long userId,

        @Schema(description = "Dados do pagamento")
        CustomerPaymentRequestDTO customerPaymentRequestDTO
) {
}
