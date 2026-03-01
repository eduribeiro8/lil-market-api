package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import com.eduribeiro8.LilMarket.entity.Restock;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.RestockMapper;
import com.eduribeiro8.LilMarket.repository.RestockRepository;
import com.eduribeiro8.LilMarket.rest.exception.RestockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestockServiceImpl implements RestockService{

    private final RestockRepository restockRepository;
    private final RestockMapper restockMapper;
    private final BatchService batchService;
    private final SupplierService supplierService;
    private final ProductService productService;

    @Override
    @Transactional
    public RestockResponseDTO save(RestockRequestDTO restockRequestDTO) {
        Restock restockToSave = new Restock();

        Supplier supplier = supplierService.findById(restockRequestDTO.supplierId());
        restockToSave.setSupplier(supplier);
        restockToSave.setInvoice(restockRequestDTO.invoice());
        restockToSave.setAmountPaid(
                restockRequestDTO.batchRequestDTOS().stream()
                        .map(a -> a.purchasePrice().multiply(a.quantityInStock()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        restockToSave.setBoughtAt(restockRequestDTO.boughtAt());

        Restock savedRestock = restockRepository.save(restockToSave);

        batchService.saveFromRestock(savedRestock, restockRequestDTO.batchRequestDTOS());

        Set<Long> products = restockRequestDTO.batchRequestDTOS()
                .stream().map(BatchRequestDTO::productId)
                .collect(Collectors.toSet());

        products.forEach(productService::calculatePriceBasedOnStock);

        return restockMapper.toResponse(savedRestock);
    }

    @Override
    public RestockResponseDTO findByIdDTO(Long restockId) {
        return restockMapper.toResponse(findById(restockId));
    }

    @Override
    public Restock findById(Long restockId) {
        return restockRepository.findById(restockId)
                        .orElseThrow(() -> new RestockNotFoundException("Compra (id = " + restockId + ") não encontrada."));
    }

    @Override
    public Page<RestockResponseDTO> getAll(Pageable pageable) {
        Page<Restock> restockPage = restockRepository.findAll(pageable);

        if (restockPage.isEmpty()){
            throw new RestockNotFoundException("Não há compras registradas");
        }

        return restockPage.map(restockMapper::toResponse);
    }
}
