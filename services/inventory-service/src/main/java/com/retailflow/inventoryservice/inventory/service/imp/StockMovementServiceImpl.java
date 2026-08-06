package com.retailflow.inventoryservice.inventory.service.imp;

import com.retailflow.inventoryservice.common.exception.InvalidRequestException;
import com.retailflow.inventoryservice.inventory.entity.StockMovement;
import com.retailflow.inventoryservice.inventory.repository.StockMovementRepository;
import com.retailflow.inventoryservice.inventory.mapper.StockMovementMapper;
import com.retailflow.inventoryservice.inventory.dto.response.StockMovementResponse;
import com.retailflow.inventoryservice.inventory.entity.MovementType;
import com.retailflow.inventoryservice.inventory.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    @Override
    public StockMovement recordMovement(Long productId,
                                        Long warehouseId,
                                        MovementType movementType,
                                        Integer quantity,
                                        String referenceType,
                                        Long referenceId,
                                        String remarks) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .movementType(movementType)
                .quantity(quantity)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .remarks(remarks)
                .build();

        return stockMovementRepository.save(movement);
    }

    @Override
    public List<StockMovementResponse> getAllMovements() {
        return stockMovementRepository.findAll().stream()
                .map(stockMovementMapper::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId).stream()
                .map(stockMovementMapper::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getMovementsByWarehouse(Long warehouseId) {
        return stockMovementRepository.findByWarehouseId(warehouseId).stream()
                .map(stockMovementMapper::toResponse)
                .toList();
    }
}
