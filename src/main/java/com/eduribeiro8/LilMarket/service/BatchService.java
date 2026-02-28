package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Restock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface BatchService {

    void saveFromRestock(Restock restock, List<BatchRequestDTO> batchRequestDTOList);

    Page<BatchResponseDTO> getAllBatchesInStock(Pageable pageable);

    Page<BatchResponseDTO> getAllBatchesByRestockId(Long restockId, Pageable pageable);

    Page<BatchResponseDTO> getAllBatchesInStockByDate(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<BatchResponseDTO> getBatchesInStockDTO(Long productId, BigDecimal quantity);

    BatchResponseDTO getBatchById(Long batchId);

    List<Batch> findBatchesInStock(Product product, BigDecimal quantity);

    List<BatchResponseDTO> findBatchesToExpireIn(int days);

    void decrementStock(Product product, BigDecimal quantity);

    List<Batch> decrementBatches(List<Batch> batches,Product product, BigDecimal quantity);

    void reportLoss(BatchLossReportRequestDTO batchLossReport);

    void invalidateBatch(Long batchId, BatchInvalidationRequestDTO batchInvalidation);
}
