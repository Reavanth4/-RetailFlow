package com.retailflow.inventoryservice.inventory.service;

import com.retailflow.inventoryservice.inventory.dto.response.StockMovementResponse;
import com.retailflow.inventoryservice.inventory.entity.MovementType;
import com.retailflow.inventoryservice.inventory.entity.StockMovement;

import java.util.List;

public interface StockMovementService {

    StockMovement recordMovement(Long productId,
                                 Long warehouseId,
                                 MovementType movementType,
                                 Integer quantity,
                                 String referenceType,
                                 Long referenceId,
                                 String remarks);

    List<StockMovementResponse> getAllMovements();

    List<StockMovementResponse> getMovementsByProduct(Long productId);

    List<StockMovementResponse> getMovementsByWarehouse(Long warehouseId);
}
