package com.retailflow.reportservice.report.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockReportResponse {

    private int threshold;
    private long totalItems;
    @Builder.Default
    private List<InventoryItemReport> items = new ArrayList<>();
}
