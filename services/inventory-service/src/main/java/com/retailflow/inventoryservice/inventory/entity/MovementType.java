package com.retailflow.inventoryservice.inventory.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum MovementType {
    PURCHASE,
    SALE,
    TRANSFER_IN,
    TRANSFER_OUT,
    ADJUSTMENT_IN,
    ADJUSTMENT_OUT,
    RETURN_IN,
    RETURN_OUT,
    DAMAGE
}
