package com.retailflow.inventoryservice.inventory.service;

import com.retailflow.inventoryservice.common.dto.PageResponse;
import com.retailflow.inventoryservice.inventory.dto.request.AdjustStockRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockInRequest;
import com.retailflow.inventoryservice.inventory.dto.request.StockOutRequest;
import com.retailflow.inventoryservice.inventory.dto.request.TransferRequest;
import com.retailflow.inventoryservice.inventory.dto.response.InventoryResponse;
import com.retailflow.inventoryservice.inventory.dto.response.TransferResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    InventoryResponse getInventoryById(Long inventoryId);

    PageResponse<InventoryResponse> getAllInventories(Long warehouseId, Pageable pageable);

    List<InventoryResponse> getInventoryByProduct(Long productId);

    List<InventoryResponse> getInventoryByWarehouse(Long warehouseId);

    InventoryResponse getInventoryByProductAndWarehouse(Long productId, Long warehouseId);

    List<InventoryResponse> getLowStock(int threshold);

    InventoryResponse stockIn(StockInRequest request);

    InventoryResponse stockOut(StockOutRequest request);

    TransferResponse transfer(TransferRequest request);

    InventoryResponse adjustStock(AdjustStockRequest request);
}
