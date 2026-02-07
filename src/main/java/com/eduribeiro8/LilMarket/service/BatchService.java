package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface BatchService {

    BatchResponseDTO save(BatchRequestDTO batch);

    Page<BatchResponseDTO> getAllBatchesInStock(Pageable pageable);

    Page<BatchResponseDTO> getAllBatchesInStockByDate(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<BatchResponseDTO> getBatchesInStockDTO(Integer productId, int quantity);

    BatchResponseDTO getBatchById(Integer batchId);

    List<Batch> findBatchesInStock(Product product, int quantity);

    List<BatchResponseDTO> findBatchesToExpireIn(int days);

    void decrementStock(Product product, int quantity);

    List<Batch> decrementBatches(List<Batch> batches,Product product, int quantity);

    void reportLoss(BatchLossReportRequestDTO batchLossReport);

    void invalidateBatch(Integer batchId, BatchInvalidationRequestDTO batchInvalidation);
}
