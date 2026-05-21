package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementBackfillResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.MovementType;
import com.eduribeiro8.LilMarket.entity.OneTimeJobExecution;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.SaleItem;
import com.eduribeiro8.LilMarket.entity.StockMovement;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.repository.OneTimeJobExecutionRepository;
import com.eduribeiro8.LilMarket.repository.ProductRepository;
import com.eduribeiro8.LilMarket.repository.SaleItemRepository;
import com.eduribeiro8.LilMarket.repository.StockMovementRepository;
import com.eduribeiro8.LilMarket.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockMovementBackfillServiceImpl implements StockMovementBackfillService {

    private static final String JOB_NAME = "stock_movement_backfill_v1";

    private final StockMovementRepository stockMovementRepository;
    private final OneTimeJobExecutionRepository oneTimeJobExecutionRepository;
    private final BatchRepository batchRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public StockMovementBackfillResponseDTO executeOneTimeBackfill() {
        if (oneTimeJobExecutionRepository.existsById(JOB_NAME)) {
            throw new BusinessException("Backfill já foi executado anteriormente.");
        }
        if (stockMovementRepository.count() > 0) {
            throw new BusinessException("A tabela stock_movement já possui dados. Backfill bloqueado para evitar duplicidade.");
        }

        List<SaleItem> saleItems = saleItemRepository.findAll();
        List<Batch> batches = batchRepository.findAll();
        List<Product> products = productRepository.findAll();
        Map<Long, Product> productById = new HashMap<>();
        Map<Long, BigDecimal> currentStockByProductId = new HashMap<>();
        for (Product product : products) {
            productById.put(product.getId(), product);
        }

        Map<Long, List<MovementEvent>> eventsByProductId = new HashMap<>();
        for (Product product : products) {
            eventsByProductId.put(product.getId(), new ArrayList<>());
        }

        for (Batch batch : batches) {
            if (batch.getProduct() == null || batch.getProduct().getId() == null) {
                continue;
            }
            currentStockByProductId.merge(batch.getProduct().getId(), nonNull(batch.getQuantityInStock()), BigDecimal::add);

            BigDecimal fallbackEntryQuantity = nonNull(batch.getQuantityInStock()).add(nonNull(batch.getQuantityLost()));
            BigDecimal entryQuantity = batch.getOriginalQuantity() != null ? batch.getOriginalQuantity() : fallbackEntryQuantity;
            OffsetDateTime restockTimestamp = resolveRestockTimestamp(batch);

            if (entryQuantity.compareTo(BigDecimal.ZERO) > 0) {
                appendEvent(eventsByProductId, batch.getProduct().getId(), new MovementEvent(
                        restockTimestamp,
                        MovementType.ENTRY,
                        entryQuantity,
                        batch.getRestock() != null ? batch.getRestock().getId() : null,
                        "Backfill: entrada por restock/lote",
                        batch.getProduct()
                ));
            }

            if (nonNull(batch.getQuantityLost()).compareTo(BigDecimal.ZERO) > 0) {
                appendEvent(eventsByProductId, batch.getProduct().getId(), new MovementEvent(
                        restockTimestamp.plusNanos(1),
                        MovementType.LOSS,
                        batch.getQuantityLost(),
                        batch.getId(),
                        "Backfill: perda por quantity_lost do lote",
                        batch.getProduct()
                ));
            }
        }

        for (SaleItem saleItem : saleItems) {
            if (saleItem.getProduct() == null || saleItem.getProduct().getId() == null || saleItem.getQuantity() == null) {
                continue;
            }
            if (saleItem.getSale() == null || saleItem.getSale().getTimestamp() == null) {
                continue;
            }

            appendEvent(eventsByProductId, saleItem.getProduct().getId(), new MovementEvent(
                    saleItem.getSale().getTimestamp(),
                    MovementType.EXIT,
                    saleItem.getQuantity(),
                    saleItem.getSale().getId(),
                    "Backfill: saída por venda",
                    saleItem.getProduct()
            ));
        }

        List<StockMovement> movementsToPersist = new ArrayList<>();
        for (Map.Entry<Long, List<MovementEvent>> entry : eventsByProductId.entrySet()) {
            List<MovementEvent> events = entry.getValue();
            events.sort(Comparator
                    .comparing(MovementEvent::timestamp)
                    .thenComparing(e -> movementPriority(e.type()))
                    .thenComparing(e -> e.referenceId() == null ? Long.MAX_VALUE : e.referenceId()));

            BigDecimal runningStock = BigDecimal.ZERO;
            for (MovementEvent event : events) {
                if (event.type() == MovementType.ENTRY) {
                    runningStock = runningStock.add(event.quantity());
                } else {
                    runningStock = runningStock.subtract(event.quantity());
                }

                movementsToPersist.add(StockMovement.builder()
                        .product(event.product())
                        .movementType(event.type())
                        .quantity(event.quantity())
                        .description(event.description())
                        .referenceId(event.referenceId())
                        .timestamp(event.timestamp())
                        .build());
            }

            Product product = productById.get(entry.getKey());

            if (product != null) {
                BigDecimal expectedStock = currentStockByProductId.getOrDefault(entry.getKey(), nonNull(product.getTotalQuantity()));
                BigDecimal adjustment = expectedStock.subtract(runningStock);

                if (adjustment.compareTo(BigDecimal.ZERO) > 0) {
                    movementsToPersist.add(StockMovement.builder()
                            .product(product)
                            .movementType(MovementType.ENTRY)
                            .quantity(adjustment)
                            .description("Backfill: ajuste de reconciliação")
                            .referenceId(null)
                            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                            .build());
                } else if (adjustment.compareTo(BigDecimal.ZERO) < 0) {
                    movementsToPersist.add(StockMovement.builder()
                            .product(product)
                            .movementType(MovementType.LOSS)
                            .quantity(adjustment.abs())
                            .description("Backfill: ajuste de reconciliação")
                            .referenceId(null)
                            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                            .build());
                }
            }
        }

        stockMovementRepository.saveAll(movementsToPersist);
        oneTimeJobExecutionRepository.save(OneTimeJobExecution.builder().jobName(JOB_NAME).build());

        return new StockMovementBackfillResponseDTO(
                JOB_NAME,
                movementsToPersist.size(),
                eventsByProductId.size(),
                "Backfill executado com sucesso."
        );
    }

    private static void appendEvent(Map<Long, List<MovementEvent>> target, Long productId, MovementEvent event) {
        target.computeIfAbsent(productId, ignored -> new ArrayList<>()).add(event);
    }

    private static BigDecimal nonNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static int movementPriority(MovementType movementType) {
        return switch (movementType) {
            case ENTRY -> 1;
            case EXIT -> 2;
            case LOSS -> 3;
        };
    }

    private static OffsetDateTime resolveRestockTimestamp(Batch batch) {
        if (batch.getRestock() != null) {
            if (batch.getRestock().getCreatedAt() != null) {
                return batch.getRestock().getCreatedAt();
            }
            if (batch.getRestock().getBoughtAt() != null) {
                return batch.getRestock().getBoughtAt()
                        .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
                        .toOffsetDateTime();
            }
        }
        return batch.getCreatedAt();
    }

    private record MovementEvent(
            OffsetDateTime timestamp,
            MovementType type,
            BigDecimal quantity,
            Long referenceId,
            String description,
            Product product
    ) {
    }
}
