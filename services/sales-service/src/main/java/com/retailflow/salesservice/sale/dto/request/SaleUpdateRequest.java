package com.retailflow.salesservice.sale.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SaleUpdateRequest {

    private Long customerId;

    private Long warehouseId;

    private LocalDate saleDate;

    private BigDecimal tax;

    private BigDecimal discount;

    private List<SaleItemRequest> items;
}
