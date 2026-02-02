package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações do pagamento retornadas")
public record CustomerPaymentResponseDTO(
        @Schema(description = "Identificador do pagamento", example = "12")
        Integer paymentId,
        @Schema(description = "Identificador do cliente", example = "5")
        Integer customerId,
        @Schema(description = "Valor pago", example = "50.00")
        BigDecimal amountPaid,
        @Schema(description = "Método de pagamento", example = "DINHEIRO")
        String paymentMethod,
        @Schema(description = "Data/hora do pagamento", example = "2026-01-30T12:00:00Z")
        OffsetDateTime paymentDate,
        @Schema(description = "Observações", example = "Pago em dinheiro no caixa")
        String notes
) {
}
