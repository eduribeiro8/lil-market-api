package com.eduribeiro8.LilMarket.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes da venda retornados")
public record SaleResponseDTO(
        @Schema(description = "Identificador da venda", example = "42")
        Long id,
        @Schema(description = "Data/hora da venda", example = "2026-01-30T12:00:00Z")
        OffsetDateTime timestamp,
        @Schema(description = "Nome do cliente", example = "Alice Silva")
        String customerName,
        @Schema(description = "Nome do vendedor", example = "João Souza")
        String sellerName,
        @Schema(description = "Itens incluídos na venda")
        List<SaleItemResponseDTO> items,
        @Schema(description = "Valor total da venda", example = "25.00")
        BigDecimal totalAmount,
        @Schema(description = "Valor pago", example = "25.00")
        BigDecimal amountPaid,
        @Schema(description = "Lucro líquido da venda", example = "5.50")
        BigDecimal netProfit,
        @Schema(description = "Troco devolvido ao cliente", example = "0.00")
        BigDecimal change,
        @Schema(description = "Status do pagamento", example = "PAID")
        String paymentStatus,
        @Schema(description = "Observações opcionais sobre a venda", example = "Não solicitou cupom fiscal")
        String notes
) {}
