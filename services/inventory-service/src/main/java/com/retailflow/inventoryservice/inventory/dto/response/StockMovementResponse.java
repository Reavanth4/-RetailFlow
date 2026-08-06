package com.retailflow.inventoryservice.inventory.dto.response;

import com.retailflow.inventoryservice.inventory.entity.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StockMovementResponse {

    private Long id;

    private Long productId;

    private Long warehouseId;

    private MovementType movementType;

    private Integer quantity;

    private String referenceType;

    private Long referenceId;

    private String remarks;

    private LocalDateTime createdAt;

    private String createdBy;
}
