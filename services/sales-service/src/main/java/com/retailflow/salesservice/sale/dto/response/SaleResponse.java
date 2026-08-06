package com.retailflow.salesservice.sale.dto.response;

import com.retailflow.salesservice.sale.entity.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SaleResponse {

    private Long id;

    private String saleNumber;

    private Long customerId;

    private Long warehouseId;

    private LocalDate saleDate;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal total;

    private SaleStatus status;

    private List<SaleItemResponse> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
