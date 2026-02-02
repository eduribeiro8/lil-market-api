package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Requisição de pagamento do cliente")
public record CustomerPaymentRequestDTO(
        @Schema(description = "Identificador do cliente", example = "5")
        Integer customerId,
        @Schema(description = "Valor pago", example = "50.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero BigDecimal amountPaid,
        @Schema(description = "Método de pagamento", example = "DINHEIRO", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank PaymentMethod paymentMethod,
        @Schema(description = "Observações", example = "Pago em dinheiro no caixa")
        String notes
) {
}
