package com.retailflow.purchaseservice.purchase.dto.response;

import com.retailflow.purchaseservice.purchase.entity.PurchaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PurchaseResponse {

    private Long id;

    private Long supplierId;

    private Long warehouseId;

    private String purchaseNumber;

    private LocalDate purchaseDate;

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal discount;

    private BigDecimal total;

    private PurchaseStatus status;

    private List<PurchaseItemResponse> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
