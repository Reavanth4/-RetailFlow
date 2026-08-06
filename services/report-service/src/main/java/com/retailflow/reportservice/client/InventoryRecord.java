package com.retailflow.reportservice.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InventoryRecord(Long id,
                              Long productId,
                              Long warehouseId,
                              Integer quantity,
                              Integer reservedQuantity,
                              Integer availableQuantity,
                              Integer reorderLevel) {
}
