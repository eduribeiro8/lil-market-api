package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.mapper.BatchMapper;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.rest.exception.BatchNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBatchCodeException;
import com.eduribeiro8.LilMarket.rest.exception.InsufficientQuantityInSaleException;
import com.eduribeiro8.LilMarket.security.logging.LoggingFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService{

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Override
    @Transactional
    public BatchResponseDTO save(BatchRequestDTO batchRequest) {
        if(batchRepository.existsByBatchCode(batchRequest.batchCode())){
            throw new DuplicateBatchCodeException("Batch (" + batchRequest.batchCode() + ") is already registered");
        }

        Batch batch = batchMapper.toEntity(batchRequest);
        Product product = productService.findProductById(batchRequest.productId());
        batch.setProduct(product);

        batch = batchRepository.save(batch);

        return batchMapper.toResponse(batch);
    }

    public Page<BatchResponseDTO> getAllBatchesInStock(Pageable pageable) {
        return batchRepository.findByQuantityInStockGreaterThan(0, pageable)
                .map(batchMapper::toResponse);
    }

    @Override
    public BatchResponseDTO getBatchById(Integer batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch not found"));

        return batchMapper.toResponse(batch);
    }

    @Override
    public List<BatchResponseDTO> getBatchesInStockDTO(Integer productId, int quantity) {
        Product product = productService.findProductById(productId);
        List<Batch> batches = batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, quantity);

        if (batches.isEmpty()){
            throw new BatchNotFoundException("Batch from product(id = " + productId + ") with quantity greater than " + quantity + " not found");
        }

        return batchMapper.toResponseList(batches);
    }

    @Override
    public List<Batch> findBatchesInStock(Product product, int quantity) {
        List<Batch> batchList = batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, 0);
        List<Batch> priorityBatches = new ArrayList<>();

        for (Batch batch: batchList){
            if (batch.getQuantityInStock() >= quantity){
                priorityBatches.add(batch);
                break;
            }else{
                priorityBatches.add(batch);
                quantity -= batch.getQuantityInStock();
            }
        }

        return priorityBatches;
    }

    @Override
    public List<BatchResponseDTO> findBatchesToExpireIn(int days) {
        LocalDate limitDate = LocalDate.now().plusDays(days);
        List<Batch> batches =  batchRepository
                .findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(0, limitDate);

        return batchMapper.toResponseList(batches);
    }

    @Override
    @Transactional
    public void decrementStock(Product product, int quantity) {
        List<Batch> batches = batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, 0);

        decrementBatches(batches, product, quantity);
    }

    public List<Batch> decrementBatches(List<Batch> batches, Product product, int quantity){
        int remaining = quantity;

        for (Batch batch : batches) {
            if (remaining <= 0) break;

            int inStock = batch.getQuantityInStock();
            int toTake = Math.min(inStock, remaining);

            batch.setQuantityInStock(inStock - toTake);
            remaining -= toTake;
        }

        if (remaining > 0) {
            throw new InsufficientQuantityInSaleException("Insufficient stock for product: " + product.getName());
        }

        return batches;
    }


    @Override
    @Transactional
    public void reportLoss(BatchLossReportRequestDTO batchLossReport) {
        Integer batchId = batchLossReport.batchId();
        Integer quantity = batchLossReport.quantity();
        String reason = batchLossReport.reason();

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch(id = " + batchId + ") not found"));
        batch.setQuantityLost(batch.getQuantityLost() + quantity);
        batch.setQuantityInStock(Math.max(0, batch.getQuantityInStock() - quantity));
        batchRepository.save(batch);
        logger.info("LOSS REPORT: Batch(id = {}) was reported with a loss of {} units with the reason of {}", batchId, quantity, reason);
    }

    @Override
    @Transactional
    public void invalidateBatch(Integer batchId, BatchInvalidationRequestDTO batchInvalidation) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException("Batch(id = " + batchId + ") not found"));
        batch.setQuantityLost(batch.getQuantityLost() + batch.getQuantityInStock());
        batch.setQuantityInStock(0);
        batchRepository.save(batch);
        logger.info("INVALIDATING BATCH: Batch(id = {}) was invalidated with the reason of {}", batchId, batchInvalidation.reason());
    }
}
