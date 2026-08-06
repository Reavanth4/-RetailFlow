package com.retailflow.salesservice.client;

public interface InventoryGateway {

    int getAvailableStock(Long productId, Long warehouseId);

    void stockOut(Long productId,
                  Long warehouseId,
                  Integer quantity,
                  String movementType,
                  String referenceType,
                  Long referenceId,
                  String remarks);

    void stockIn(Long productId,
                 Long warehouseId,
                 Integer quantity,
                 String movementType,
                 String referenceType,
                 Long referenceId,
                 String remarks);
}
