package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import com.eduribeiro8.LilMarket.entity.Restock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestockService {
    
    RestockResponseDTO save(RestockRequestDTO restockRequestDTO);
    
    Restock findById(Long restockId);

    RestockResponseDTO findByIdDTO(Long restockId);
    
    Page<RestockResponseDTO> getAll(Pageable pageable);
}
