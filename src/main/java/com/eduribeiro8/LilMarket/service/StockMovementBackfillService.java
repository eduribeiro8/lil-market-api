package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementBackfillResponseDTO;

public interface StockMovementBackfillService {
    StockMovementBackfillResponseDTO executeOneTimeBackfill();
}
