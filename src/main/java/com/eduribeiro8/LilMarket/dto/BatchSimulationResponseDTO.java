package com.eduribeiro8.LilMarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Resposta da simulação de preço e custo")
public record BatchSimulationResponseDTO(
        @Schema(description = "Preço de venda atual", example = "10.00")
        BigDecimal currentSellingPrice,
        @Schema(description = "Preço de venda simulado", example = "12.50")
        BigDecimal simulatedSellingPrice,
        @Schema(description = "Custo médio atual", example = "5.00")
        BigDecimal currentAverageCost,
        @Schema(description = "Custo médio simulado", example = "6.25")
        BigDecimal simulatedAverageCost
) {
}
