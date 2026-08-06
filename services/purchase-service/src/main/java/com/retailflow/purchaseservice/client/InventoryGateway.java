package com.retailflow.purchaseservice.client;

public interface InventoryGateway {

    void stockIn(Long productId,
                 Long warehouseId,
                 Integer quantity,
                 String movementType,
                 String referenceType,
                 Long referenceId,
                 String remarks);

    void stockOut(Long productId,
                  Long warehouseId,
                  Integer quantity,
                  String movementType,
                  String referenceType,
                  Long referenceId,
                  String remarks);
}
