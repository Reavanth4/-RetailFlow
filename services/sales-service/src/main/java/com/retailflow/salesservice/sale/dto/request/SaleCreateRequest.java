package com.retailflow.salesservice.sale.dto.request;

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
public class SaleCreateRequest {

    @NotNull(message = "Customer is required")
    private Long customerId;

    @NotNull(message = "Warehouse is required")
    private Long warehouseId;

    private LocalDate saleDate;

    @PositiveOrZero(message = "Tax cannot be negative")
    private BigDecimal tax;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discount;

    @Valid
    @NotEmpty(message = "Sale must contain at least one item")
    private List<SaleItemRequest> items;
}
