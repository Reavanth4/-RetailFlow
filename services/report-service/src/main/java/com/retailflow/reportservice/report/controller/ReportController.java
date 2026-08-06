package com.retailflow.reportservice.report.controller;

import com.retailflow.reportservice.common.response.ApiResponse;
import com.retailflow.reportservice.report.dto.response.*;
import com.retailflow.reportservice.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public ApiResponse<SalesReportResponse> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ApiResponse.success(
                "Sales report generated successfully",
                reportService.getSalesReport(from, to)
        );
    }

    @GetMapping("/purchases")
    public ApiResponse<PurchaseReportResponse> getPurchaseReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ApiResponse.success(
                "Purchase report generated successfully",
                reportService.getPurchaseReport(from, to)
        );
    }

    @GetMapping("/inventory")
    public ApiResponse<InventoryReportResponse> getInventoryReport() {

        return ApiResponse.success(
                "Inventory report generated successfully",
                reportService.getInventoryReport()
        );
    }

    @GetMapping("/profit")
    public ApiResponse<ProfitReportResponse> getProfitReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ApiResponse.success(
                "Profit report generated successfully",
                reportService.getProfitReport(from, to)
        );
    }

    @GetMapping("/low-stock")
    public ApiResponse<LowStockReportResponse> getLowStockReport(
            @RequestParam(defaultValue = "10") int threshold) {

        return ApiResponse.success(
                "Low-stock report generated successfully",
                reportService.getLowStockReport(threshold)
        );
    }
}
