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
public class PurchaseReportResponse {

    private LocalDate from;
    private LocalDate to;
    private long totalPurchases;
    private long totalItemsPurchased;
    private BigDecimal totalSpent;
    @Builder.Default
    private List<TopProductReport> topProducts = new ArrayList<>();
}
