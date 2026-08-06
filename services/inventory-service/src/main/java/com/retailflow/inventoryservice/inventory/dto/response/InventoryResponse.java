package com.retailflow.inventoryservice.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryResponse {

    private Long id;

    private Long productId;

    private Long warehouseId;

    private Integer quantity;

    private Integer reservedQuantity;

    private Integer availableQuantity;

    private Integer reorderLevel;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
