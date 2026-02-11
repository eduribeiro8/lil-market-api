package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestockService {
    
    RestockResponseDTO save(RestockRequestDTO restockRequestDTO);
    
    RestockResponseDTO findById(Integer restockId);
    
    Page<RestockResponseDTO> getAll(Pageable pageable);
}
