package com.retailflow.purchaseservice.purchase.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseItemResponse {

    private Long id;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal tax;

    private BigDecimal discount;

    private BigDecimal total;
}
