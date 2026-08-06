package com.retailflow.purchaseservice.purchase.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseCreateRequest {

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    private LocalDate purchaseDate;

    @PositiveOrZero(message = "Tax cannot be negative")
    private BigDecimal tax;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discount;

    @Valid
    @NotEmpty(message = "Purchase must contain at least one item")
    private List<PurchaseItemRequest> items;
}
