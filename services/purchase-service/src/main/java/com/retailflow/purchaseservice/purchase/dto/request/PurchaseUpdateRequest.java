package com.retailflow.purchaseservice.purchase.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseUpdateRequest {

    private Long supplierId;

    private Long warehouseId;

    private LocalDate purchaseDate;

    private BigDecimal tax;

    private BigDecimal discount;

    private List<PurchaseItemRequest> items;
}
