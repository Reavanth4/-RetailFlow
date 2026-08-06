package com.retailflow.reportservice.report.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemReport {

    private Long productId;
    private String productName;
    private Long warehouseId;
    private Integer quantity;
    private Integer availableQuantity;
    private Integer reorderLevel;
    private BigDecimal stockValue;
    private String status;
}
