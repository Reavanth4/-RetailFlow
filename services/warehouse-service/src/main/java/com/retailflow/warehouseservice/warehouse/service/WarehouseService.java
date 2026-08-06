package com.retailflow.warehouseservice.warehouse.service;

import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseCreateRequest;
import com.retailflow.warehouseservice.warehouse.dto.request.WarehouseUpdateRequest;
import com.retailflow.warehouseservice.warehouse.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseCreateRequest request);

    WarehouseResponse updateWarehouse(Long warehouseId, WarehouseUpdateRequest request);

    WarehouseResponse getWarehouseById(Long warehouseId);

    List<WarehouseResponse> getAllWarehouses();

    void deleteWarehouse(Long warehouseId);
}
