package com.retailflow.reportservice.report.service;

import com.retailflow.reportservice.report.dto.response.*;

import java.time.LocalDate;

public interface ReportService {

    SalesReportResponse getSalesReport(LocalDate from, LocalDate to);

    PurchaseReportResponse getPurchaseReport(LocalDate from, LocalDate to);

    InventoryReportResponse getInventoryReport();

    ProfitReportResponse getProfitReport(LocalDate from, LocalDate to);

    LowStockReportResponse getLowStockReport(int threshold);
}
