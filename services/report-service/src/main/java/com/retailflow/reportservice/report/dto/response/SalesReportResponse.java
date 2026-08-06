package com.retailflow.reportservice.report.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {

    private LocalDate from;
    private LocalDate to;
    private long totalSales;
    private long totalItemsSold;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    @Builder.Default
    private List<TopProductReport> topProducts = new ArrayList<>();
    @Builder.Default
    private List<WarehouseSalesReport> byWarehouse = new ArrayList<>();
}
