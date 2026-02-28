package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SupplierRequestDTO;
import com.eduribeiro8.LilMarket.dto.SupplierResponseDTO;
import com.eduribeiro8.LilMarket.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

    SupplierResponseDTO save(SupplierRequestDTO supplierRequestDTO);

    SupplierResponseDTO findByIdDTO(Long id);

    Supplier findById(Long id);

    Page<SupplierResponseDTO> getAll(Pageable pageable);
}
