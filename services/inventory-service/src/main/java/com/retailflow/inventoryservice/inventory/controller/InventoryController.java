package com.retailflow.inventoryservice.inventory.controller;

import com.retailflow.inventoryservice.common.dto.PageResponse;
import com.retailflow.inventoryservice.common.response.ApiResponse;
import com.retailflow.inventoryservice.inventory.dto.request.AdjustStockRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockInRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockOutRequest;
import com.retailflow.inventoryservice.inventory.dto.request.TransferRequest;
import com.retailflow.inventoryservice.inventory.dto.response.InventoryResponse;
import com.retailflow.inventoryservice.inventory.dto.response.TransferResponse;
import com.retailflow.inventoryservice.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ApiResponse<PageResponse<InventoryResponse>> getAllInventories(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParts = sort.split(",");
        String sortBy = sortParts[0];
        Sort.Direction sortDir = (sortParts.length > 1
                && sortParts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));

        return ApiResponse.success(
                "Inventory fetched successfully",
                inventoryService.getAllInventories(warehouseId, pageable)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<InventoryResponse> getInventoryById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Inventory fetched successfully",
                inventoryService.getInventoryById(id)
        );
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<InventoryResponse>> getInventoryByProduct(
            @PathVariable Long productId) {

        return ApiResponse.success(
                "Inventory fetched successfully",
                inventoryService.getInventoryByProduct(productId)
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ApiResponse<List<InventoryResponse>> getInventoryByWarehouse(
            @PathVariable Long warehouseId) {

        return ApiResponse.success(
                "Inventory fetched successfully",
                inventoryService.getInventoryByWarehouse(warehouseId)
        );
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ApiResponse<InventoryResponse> getInventoryByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {

        return ApiResponse.success(
                "Inventory fetched successfully",
                inventoryService.getInventoryByProductAndWarehouse(productId, warehouseId)
        );
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<InventoryResponse>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {

        return ApiResponse.success(
                "Low stock inventory fetched successfully",
                inventoryService.getLowStock(threshold)
        );
    }

    @PostMapping("/stock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InventoryResponse> stockIn(
            @Valid @RequestBody StockInRequest request) {

        return ApiResponse.success(
                "Stock added successfully",
                inventoryService.stockIn(request)
        );
    }

    @PostMapping("/stock-out")
    public ApiResponse<InventoryResponse> stockOut(
            @Valid @RequestBody StockOutRequest request) {

        return ApiResponse.success(
                "Stock deducted successfully",
                inventoryService.stockOut(request)
        );
    }

    @PostMapping("/transfer")
    public ApiResponse<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request) {

        return ApiResponse.success(
                "Stock transferred successfully",
                inventoryService.transfer(request)
        );
    }

    @PostMapping("/adjust")
    public ApiResponse<InventoryResponse> adjustStock(
            @Valid @RequestBody AdjustStockRequest request) {

        return ApiResponse.success(
                "Stock adjusted successfully",
                inventoryService.adjustStock(request)
        );
    }
}
