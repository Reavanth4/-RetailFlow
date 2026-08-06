package com.retailflow.reportservice.report.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSalesReport {

    private Long warehouseId;
    private long numberOfSales;
    private BigDecimal revenue;
}
