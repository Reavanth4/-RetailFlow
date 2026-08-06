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
public class InventoryReportResponse {

    private LocalDate asOf;
    private long totalRows;
    private long totalQuantity;
    private BigDecimal totalStockValue;
    @Builder.Default
    private List<InventoryItemReport> items = new ArrayList<>();
}
