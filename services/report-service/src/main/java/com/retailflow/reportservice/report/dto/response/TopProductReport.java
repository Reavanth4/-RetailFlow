package com.retailflow.reportservice.report.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductReport {

    private Long productId;
    private String productName;
    private long quantity;
    private BigDecimal revenue;
}
