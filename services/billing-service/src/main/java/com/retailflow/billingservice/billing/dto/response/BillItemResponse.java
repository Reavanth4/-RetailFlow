package com.retailflow.billingservice.billing.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}
