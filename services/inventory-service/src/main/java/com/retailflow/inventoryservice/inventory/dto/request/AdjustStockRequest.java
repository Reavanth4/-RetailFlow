package com.retailflow.inventoryservice.inventory.dto.request;

import com.retailflow.inventoryservice.inventory.entity.AdjustmentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustStockRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
