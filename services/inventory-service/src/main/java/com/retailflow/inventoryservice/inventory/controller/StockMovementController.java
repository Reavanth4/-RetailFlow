package com.retailflow.inventoryservice.inventory.controller;

import com.retailflow.inventoryservice.common.response.ApiResponse;
import com.retailflow.inventoryservice.inventory.dto.response.StockMovementResponse;
import com.retailflow.inventoryservice.inventory.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ApiResponse<List<StockMovementResponse>> getAllMovements() {

        return ApiResponse.success(
                "Stock movements fetched successfully",
                stockMovementService.getAllMovements()
        );
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<StockMovementResponse>> getMovementsByProduct(
            @PathVariable Long productId) {

        return ApiResponse.success(
                "Stock movements fetched successfully",
                stockMovementService.getMovementsByProduct(productId)
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ApiResponse<List<StockMovementResponse>> getMovementsByWarehouse(
            @PathVariable Long warehouseId) {

        return ApiResponse.success(
                "Stock movements fetched successfully",
                stockMovementService.getMovementsByWarehouse(warehouseId)
        );
    }
}
