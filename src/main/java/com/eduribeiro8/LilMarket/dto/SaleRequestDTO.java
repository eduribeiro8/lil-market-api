package com.eduribeiro8.LilMarket.dto;

import com.eduribeiro8.LilMarket.entity.PaymentMethod;
import com.eduribeiro8.LilMarket.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Requisição de venda")
public record SaleRequestDTO(
        @Schema(description = "Identificador do cliente", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long customerId,
        @Schema(description = "Identificador do usuário (vendedor)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long userId,
        @Schema(description = "Itens da venda", example = "[]")
        @NotNull List<SaleItemRequestDTO> items,
        @Schema(description = "Valor pago pelo cliente", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull BigDecimal amountPaid,
        @Schema(description = "Venda em conta (a prazo)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Boolean isOnAccount,
        @Schema(description = "Observações", example = "Embalar para presente")
        String notes,
        @Schema(description = "Status do pagamento", example = "PAID")
        PaymentStatus paymentStatus,
        @Schema(description = "Método de pagamento utilizado", example = "DINHEIRO")
        PaymentMethod paymentMethod
) {}
