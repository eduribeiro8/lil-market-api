package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Restock;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.BatchMapper;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.rest.exception.BatchNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBatchCodeException;
import com.eduribeiro8.LilMarket.rest.exception.InsufficientQuantityInSaleException;
import com.eduribeiro8.LilMarket.rest.exception.InvalidDateIntervalException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService{

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final ProductService productService;
    private final SupplierService supplierService;
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);


    @Override
    @Transactional
    public void saveFromRestock(Restock restock, List<BatchRequestDTO> batchRequestDTOList) {
        Supplier supplier = restock.getSupplier();
        Set<String> currentBatchCodes = new HashSet<>();

        List<Batch> batches = batchRequestDTOList.stream().map(batchRequest -> {
            if (batchRequest.batchCode() != null && !batchRequest.batchCode().isBlank()) {
                if (currentBatchCodes.contains(batchRequest.batchCode())){
                    throw new DuplicateBatchCodeException("O código do lote (" + batchRequest.batchCode() + ") se repete para dois ou mais lotes nesta compra.");
                }
                else if (batchRepository.existsByBatchCode(batchRequest.batchCode())) {
                    throw new DuplicateBatchCodeException("Batch (" + batchRequest.batchCode() + ") is already registered");
                }
            }

            Batch batch = batchMapper.toEntity(batchRequest);
            Product product = productService.findProductById(batchRequest.productId());

            batch.setBatchCode(batchRequest.batchCode() == null || batchRequest.batchCode().isBlank() ?
                            generateAutomaticBatchCode(product.getId(), batchRequest.expirationDate(), currentBatchCodes) :
                            batch.getBatchCode()
                    );

            currentBatchCodes.add(batch.getBatchCode());

            batch.setProduct(product);
            batch.setSupplier(supplier);
            batch.setRestock(restock);

            product.setTotalQuantity(product.getTotalQuantity().add(batchRequest.quantityInStock()));

            return batch;
        }).toList();

        batchRepository.saveAll(batches);
    }

    @Override
    public Page<BatchResponseDTO> getAllBatchesInStockByDate(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate.isAfter(endDate)){
            throw new InvalidDateIntervalException("A data final não pode ser anterior à data inicial");
        }

        Page<Batch> batches = batchRepository.findByQuantityInStockGreaterThanAndExpirationDateBetween(BigDecimal.ZERO, startDate, endDate, pageable);

        if (batches.isEmpty()){
            throw new BatchNotFoundException("Não há lotes entre " + startDate + " e " + endDate + ".");
        }

        return batches.map(batchMapper::toResponse);
    }

    public Page<BatchResponseDTO> getAllBatchesInStock(Pageable pageable) {
        return batchRepository.findByQuantityInStockGreaterThan(BigDecimal.ZERO, pageable)
                .map(batchMapper::toResponse);
    }

    @Override
    public Page<BatchResponseDTO> getAllBatchesByRestockId(int restockId, Pageable pageable) {

        Page<Batch> batches = batchRepository.findByRestockId(restockId, pageable);

        if (batches.isEmpty()){
            throw new BatchNotFoundException("Não há lotes para o Restock (id = " + restockId + ").");
        }

        return batches.map(batchMapper::toResponse);
    }

    @Override
    public BatchResponseDTO getBatchById(Integer batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch not found"));

        return batchMapper.toResponse(batch);
    }

    @Override
    public List<BatchResponseDTO> getBatchesInStockDTO(Integer productId, BigDecimal quantity) {
        Product product = productService.findProductById(productId);
        List<Batch> batches = batchRepository.findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(product, quantity);

        if (batches.isEmpty()){
            throw new BatchNotFoundException("Batch from product(id = " + productId + ") with quantity greater than " + quantity + " not found");
        }

        return batchMapper.toResponseList(batches);
    }

    @Override
    public List<Batch> findBatchesInStock(Product product, BigDecimal quantity) {
        List<Batch> batchList = batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO);
        List<Batch> priorityBatches = new ArrayList<>();

        for (Batch batch: batchList){
            if (batch.getQuantityInStock().compareTo(quantity) >= 0){
                priorityBatches.add(batch);
                break;
            }else{
                priorityBatches.add(batch);
                quantity = quantity.subtract(batch.getQuantityInStock());
            }
        }

        return priorityBatches;
    }

    @Override
    public List<BatchResponseDTO> findBatchesToExpireIn(int days) {
        LocalDate limitDate = LocalDate.now().plusDays(days);
        List<Batch> batches =  batchRepository
                .findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(BigDecimal.ZERO, limitDate);

        return batchMapper.toResponseList(batches);
    }

    @Override
    @Transactional
    public void decrementStock(Product product, BigDecimal quantity) {
        List<Batch> batches = batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO);

        decrementBatches(batches, product, quantity);
    }

    public List<Batch> decrementBatches(List<Batch> batches, Product product, BigDecimal quantity) {
        BigDecimal remaining = quantity;

        for (Batch batch : batches) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal inStock = batch.getQuantityInStock();

            BigDecimal toTake = inStock.min(remaining);

            batch.setQuantityInStock(inStock.subtract(toTake));

            remaining = remaining.subtract(toTake);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientQuantityInSaleException("Insufficient stock for product: " + product.getName());
        }

        product.setTotalQuantity(product.getTotalQuantity().subtract(quantity));

        return batches;
    }


    @Override
    @Transactional
    public void reportLoss(BatchLossReportRequestDTO batchLossReport) {
        Integer batchId = batchLossReport.batchId();
        BigDecimal quantity = batchLossReport.quantity();
        String reason = batchLossReport.reason();

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch(id = " + batchId + ") not found"));
        batch.setQuantityLost(batch.getQuantityLost().add(quantity));
        batch.setQuantityInStock(batch.getQuantityInStock().subtract(quantity).max(BigDecimal.ZERO));

        Product product = batch.getProduct();
        product.setTotalQuantity(product.getTotalQuantity().subtract(quantity));

        batchRepository.save(batch);
        logger.info("LOSS REPORT: Batch(id = {}) was reported with a loss of {} units with the reason of {}", batchId, quantity, reason);
    }

    @Override
    @Transactional
    public void invalidateBatch(Integer batchId, BatchInvalidationRequestDTO batchInvalidation) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch(id = " + batchId + ") not found"));
        batch.setQuantityLost(batch.getQuantityLost().add(batch.getQuantityInStock()));
        batch.setQuantityInStock(BigDecimal.ZERO);

        Product product = batch.getProduct();
        product.setTotalQuantity(product.getTotalQuantity().subtract(batch.getQuantityLost()));

        batchRepository.save(batch);
        logger.info("INVALIDATING BATCH: Batch(id = {}) was invalidated with the reason of {}", batchId, batchInvalidation.reason());
    }

    private String generateAutomaticBatchCode(Integer productId, LocalDate expirationDate, Set<String> currentBatchCodes) {
        String expiryPart = expirationDate.format(DateTimeFormatter.ofPattern("yyMMdd"));

        while(true){
            int suffix = java.util.concurrent.ThreadLocalRandom.current().nextInt(100, 1000);

            String barcode = String.format("P%d-%d-V%s", productId, suffix, expiryPart);

            if (!currentBatchCodes.contains(barcode) && !batchRepository.existsByBatchCode(barcode)){
                return barcode;
            }
        }
    }
}
