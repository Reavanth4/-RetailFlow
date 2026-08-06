package com.retailflow.inventoryservice.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransferResponse {

    private Long productId;

    private Long sourceWarehouseId;

    private Long destinationWarehouseId;

    private Integer quantity;

    private Long transferOutMovementId;

    private Long transferInMovementId;

    private String message;
}
