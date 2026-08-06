package com.retailflow.purchaseservice.purchase.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierReturnItemResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String reason;
}
