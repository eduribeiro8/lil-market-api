package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping("/batch")
    public BatchResponseDTO save(@RequestBody BatchRequestDTO batchRequestDTO){
        return batchService.save(batchRequestDTO);
    }

    @GetMapping("/batch")
    public List<BatchResponseDTO> getAllBatchesInStock(){
        return batchService.getAllBatchesInStock();
    }

    @GetMapping("/batch/in-stock")
    public List<BatchResponseDTO> getBatchesInStock(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity){
        return batchService.getBatchesInStockDTO(productId, quantity);
    }

    @PostMapping("/batch/report-loss")
    public ResponseEntity<Void> reportLoss(@RequestBody @Valid BatchLossReportRequestDTO request) {
        batchService.reportLoss(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch/invalidate-stock")
    public ResponseEntity<Void> invalidateStock(
            @RequestParam Integer batchId,
            @RequestBody @Valid BatchInvalidationRequestDTO request) {
        batchService.invalidateBatch(batchId, request);
        return ResponseEntity.noContent().build();
    }
}
