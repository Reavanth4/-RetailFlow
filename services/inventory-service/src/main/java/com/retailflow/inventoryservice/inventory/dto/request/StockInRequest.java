package com.retailflow.inventoryservice.inventory.dto.request;

import com.retailflow.inventoryservice.inventory.entity.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockInRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Movement type is required")
    private MovementType movementType;

    @Size(max = 30, message = "Reference type cannot exceed 30 characters")
    private String referenceType;

    private Long referenceId;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}
