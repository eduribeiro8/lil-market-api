package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementRequestDTO;
import com.eduribeiro8.LilMarket.dto.StockMovementResponseDTO;
import com.eduribeiro8.LilMarket.entity.MovementType;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.StockMovement;
import com.eduribeiro8.LilMarket.repository.StockMovementRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidDateIntervalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    @Override
    public Page<StockMovementResponseDTO> getStockMovement(StockMovementRequestDTO filter, Pageable pageable) {
        LocalDate startDate = filter.startDate();
        LocalDate endDate = filter.endDate();
        OffsetDateTime startUtc = null;
        OffsetDateTime endUtc = null;

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidDateIntervalException("A data final não pode ser anterior à data inicial");
        }

        if (startDate != null) {
            ZonedDateTime startOfBrtDay = startDate.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
            startUtc = startOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
        }

        if (endDate != null) {
            ZonedDateTime endOfBrtDay = endDate.atTime(23, 59, 59).atZone(ZoneId.of("America/Sao_Paulo"));
            endUtc = endOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
        }

        Page<StockMovement> page = stockMovementRepository.findStockMovementByProductOptionalType(
                filter.productId(), filter.movementType(), startUtc, endUtc, pageable);

        if (page.isEmpty()) {
            return Page.empty(pageable);
        }

        List<StockMovementResponseDTO> dtos = new ArrayList<>(page.getContent().size());
        for (StockMovement m : page.getContent()) {
            BigDecimal stockBefore = stockMovementRepository.sumMovementsBefore(
                    filter.productId(), m.getTimestamp(), m.getId());
            BigDecimal quantityInStock = m.getMovementType() == MovementType.ENTRY
                    ? stockBefore.add(m.getQuantity())
                    : stockBefore.subtract(m.getQuantity());

            dtos.add(new StockMovementResponseDTO(
                    m.getId(),
                    m.getProduct().getId(),
                    m.getProduct().getName(),
                    m.getMovementType(),
                    m.getQuantity(),
                    quantityInStock,
                    m.getReferenceId(),
                    m.getDescription(),
                    m.getTimestamp()
            ));
        }

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public void recordEntry(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp) {
        record(product, MovementType.ENTRY, quantity, referenceId, description, timestamp);
    }

    @Override
    public void recordExit(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp) {
        record(product, MovementType.EXIT, quantity, referenceId, description, timestamp);
    }

    @Override
    public void recordLoss(Product product, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp) {
        record(product, MovementType.LOSS, quantity, referenceId, description, timestamp);
    }

    private void record(Product product, MovementType movementType, BigDecimal quantity, Long referenceId, String description, OffsetDateTime timestamp) {
        if (product == null || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        stockMovementRepository.save(StockMovement.builder()
                .product(product)
                .movementType(movementType)
                .quantity(quantity)
                .referenceId(referenceId)
                .description(description)
                .timestamp(timestamp)
                .build());
    }
}
