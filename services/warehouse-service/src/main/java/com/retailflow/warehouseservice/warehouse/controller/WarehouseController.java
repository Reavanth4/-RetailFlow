package com.retailflow.warehouseservice.warehouse.controller;

import com.retailflow.warehouseservice.common.response.ApiResponse;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseCreateRequest;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseUpdateRequest;
import com.retailflow.warehouseservice.warehouse.dto.response.WarehouseResponse;
import com.retailflow.warehouseservice.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WarehouseResponse> createWarehouse(
            @Valid @RequestBody WarehouseCreateRequest request) {

        return ApiResponse.success(
                "Warehouse created successfully",
                warehouseService.createWarehouse(request)
        );
    }

    @GetMapping
    public ApiResponse<List<WarehouseResponse>> getAllWarehouses() {

        return ApiResponse.success(
                "Warehouses fetched successfully",
                warehouseService.getAllWarehouses()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponse> getWarehouseById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Warehouse fetched successfully",
                warehouseService.getWarehouseById(id)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateRequest request) {

        return ApiResponse.success(
                "Warehouse updated successfully",
                warehouseService.updateWarehouse(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteWarehouse(
            @PathVariable Long id) {

        warehouseService.deleteWarehouse(id);

        return ApiResponse.success(
                "Warehouse deleted successfully",
                "Deleted Successfully"
        );
    }
}
