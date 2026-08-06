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
public class ProfitReportResponse {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal revenue;
    private BigDecimal costOfGoods;
    private BigDecimal grossProfit;
    private BigDecimal marginPercentage;
    @Builder.Default
    private List<ProfitItemReport> byProduct = new ArrayList<>();
}
